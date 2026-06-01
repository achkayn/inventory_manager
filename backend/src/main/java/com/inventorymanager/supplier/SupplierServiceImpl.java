package com.inventorymanager.supplier;

import java.util.List;

import com.inventorymanager.supplier.dto.SupplierRequest;
import com.inventorymanager.supplier.dto.SupplierResponse;
import com.inventorymanager.common.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

	private final SupplierRepository supplierRepository;

	public SupplierServiceImpl(SupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SupplierResponse> getAllSuppliers() {
		return supplierRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SupplierResponse getSupplierById(Long id) {
		return toResponse(findSupplier(id));
	}

	@Override
	@Transactional
	public SupplierResponse createSupplier(SupplierRequest request) {
		validateUniqueEmail(request.getEmail(), null);
		Supplier supplier = new Supplier();
		applyRequest(supplier, request);
		return toResponse(supplierRepository.save(supplier));
	}

	@Override
	@Transactional
	public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
		Supplier supplier = findSupplier(id);
		validateUniqueEmail(request.getEmail(), id);
		applyRequest(supplier, request);
		return toResponse(supplierRepository.save(supplier));
	}

	@Override
	@Transactional
	public void deleteSupplier(Long id) {
		Supplier supplier = findSupplier(id);
		supplierRepository.delete(supplier);
	}

	private Supplier findSupplier(Long id) {
		return supplierRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Supplier not found with id " + id));
	}

	private void validateUniqueEmail(String email, Long currentId) {
		if (email == null || email.isBlank()) {
			return;
		}

		supplierRepository.findByEmail(email)
				.filter(supplier -> currentId == null || !supplier.getId().equals(currentId))
				.ifPresent(supplier -> {
					throw new ConflictException("Supplier email already exists");
				});
	}

	private void applyRequest(Supplier supplier, SupplierRequest request) {
		supplier.setName(request.getName());
		supplier.setEmail(request.getEmail());
		supplier.setPhone(request.getPhone());
		supplier.setAddress(request.getAddress());
	}

	private SupplierResponse toResponse(Supplier supplier) {
		return new SupplierResponse(
				supplier.getId(),
				supplier.getName(),
				supplier.getEmail(),
				supplier.getPhone(),
				supplier.getAddress());
	}
}
