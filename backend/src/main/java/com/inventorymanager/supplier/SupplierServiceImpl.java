package com.inventorymanager.supplier;

import java.util.List;

import com.inventorymanager.supplier.dto.SupplierRequest;
import com.inventorymanager.supplier.dto.SupplierResponse;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

	@Override
	public List<SupplierResponse> getAllSuppliers() {
		return null;
	}

	@Override
	public SupplierResponse getSupplierById(Long id) {
		return null;
	}

	@Override
	public SupplierResponse createSupplier(SupplierRequest request) {
		return null;
	}

	@Override
	public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
		return null;
	}

	@Override
	public void deleteSupplier(Long id) {
	}
}
