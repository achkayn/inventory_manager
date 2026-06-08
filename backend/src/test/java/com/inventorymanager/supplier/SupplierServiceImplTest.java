package com.inventorymanager.supplier;

import com.inventorymanager.common.ConflictException;
import com.inventorymanager.supplier.dto.SupplierRequest;
import com.inventorymanager.supplier.dto.SupplierResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier sampleSupplier() {
        return new Supplier(1L, "ACME Corp", "acme@example.com", "555-1234", "123 Main St");
    }

    private SupplierRequest sampleRequest() {
        return new SupplierRequest("ACME Corp", "acme@example.com", "555-1234", "123 Main St");
    }

    // --- getAllSuppliers ---

    @Test
    void getAllSuppliers_returnsMappedList() {
        when(supplierRepository.findAll()).thenReturn(List.of(
                sampleSupplier(),
                new Supplier(2L, "Beta Ltd", "beta@example.com", "555-5678", "456 Ave")
        ));

        List<SupplierResponse> result = supplierService.getAllSuppliers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("ACME Corp");
        assertThat(result.get(1).getEmail()).isEqualTo("beta@example.com");
    }

    @Test
    void getAllSuppliers_emptyRepository_returnsEmptyList() {
        when(supplierRepository.findAll()).thenReturn(List.of());

        assertThat(supplierService.getAllSuppliers()).isEmpty();
    }

    // --- getSupplierById ---

    @Test
    void getSupplierById_found_returnsResponse() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier()));

        SupplierResponse result = supplierService.getSupplierById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("ACME Corp");
        assertThat(result.getEmail()).isEqualTo("acme@example.com");
    }

    @Test
    void getSupplierById_notFound_throwsEntityNotFoundException() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- createSupplier ---

    @Test
    void createSupplier_uniqueEmail_savesAndReturnsResponse() {
        when(supplierRepository.findByEmail("acme@example.com")).thenReturn(Optional.empty());
        when(supplierRepository.save(any(Supplier.class))).thenReturn(sampleSupplier());

        SupplierResponse result = supplierService.createSupplier(sampleRequest());

        assertThat(result.getName()).isEqualTo("ACME Corp");
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void createSupplier_duplicateEmail_throwsConflictException() {
        when(supplierRepository.findByEmail("acme@example.com")).thenReturn(Optional.of(sampleSupplier()));

        assertThatThrownBy(() -> supplierService.createSupplier(sampleRequest()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email already exists");

        verify(supplierRepository, never()).save(any());
    }

    @Test
    void createSupplier_nullEmail_skipsEmailValidation() {
        SupplierRequest req = new SupplierRequest("NoEmail Corp", null, "555-9999", "456 Ave");
        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(new Supplier(2L, "NoEmail Corp", null, "555-9999", "456 Ave"));

        SupplierResponse result = supplierService.createSupplier(req);

        assertThat(result.getName()).isEqualTo("NoEmail Corp");
        verify(supplierRepository, never()).findByEmail(any());
    }

    @Test
    void createSupplier_blankEmail_skipsEmailValidation() {
        SupplierRequest req = new SupplierRequest("BlankEmail Corp", "  ", "555-9999", "789 Blvd");
        when(supplierRepository.save(any(Supplier.class)))
                .thenReturn(new Supplier(3L, "BlankEmail Corp", "  ", "555-9999", "789 Blvd"));

        SupplierResponse result = supplierService.createSupplier(req);

        assertThat(result.getName()).isEqualTo("BlankEmail Corp");
        verify(supplierRepository, never()).findByEmail(any());
    }

    // --- updateSupplier ---

    @Test
    void updateSupplier_sameEmailSameId_noConflict() {
        Supplier existing = sampleSupplier();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(supplierRepository.findByEmail("acme@example.com")).thenReturn(Optional.of(existing));
        when(supplierRepository.save(existing)).thenReturn(existing);

        SupplierResponse result = supplierService.updateSupplier(1L, sampleRequest());

        assertThat(result.getEmail()).isEqualTo("acme@example.com");
    }

    @Test
    void updateSupplier_emailBelongsToDifferentSupplier_throwsConflictException() {
        Supplier existing = sampleSupplier();
        Supplier other = new Supplier(2L, "Other Corp", "acme@example.com", "555-0000", "Other St");
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(supplierRepository.findByEmail("acme@example.com")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> supplierService.updateSupplier(1L, sampleRequest()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email already exists");

        verify(supplierRepository, never()).save(any());
    }

    @Test
    void updateSupplier_notFound_throwsEntityNotFoundException() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.updateSupplier(99L, sampleRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- deleteSupplier ---

    @Test
    void deleteSupplier_found_deletesSuccessfully() {
        Supplier supplier = sampleSupplier();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).delete(supplier);
    }

    @Test
    void deleteSupplier_notFound_throwsEntityNotFoundException() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.deleteSupplier(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
