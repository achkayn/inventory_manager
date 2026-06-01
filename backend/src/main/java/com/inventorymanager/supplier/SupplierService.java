package com.inventorymanager.supplier;

import java.util.List;

import com.inventorymanager.supplier.dto.SupplierRequest;
import com.inventorymanager.supplier.dto.SupplierResponse;

public interface SupplierService {

	List<SupplierResponse> getAllSuppliers();

	SupplierResponse getSupplierById(Long id);

	SupplierResponse createSupplier(SupplierRequest request);

	SupplierResponse updateSupplier(Long id, SupplierRequest request);

	void deleteSupplier(Long id);
}
