package com.inventorymanager.supplier;

import java.util.List;

import com.inventorymanager.common.ApiResponse;
import com.inventorymanager.supplier.dto.SupplierRequest;
import com.inventorymanager.supplier.dto.SupplierResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

	private final SupplierService supplierService;

	public SupplierController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
		List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
		return ResponseEntity.ok(new ApiResponse<>(true, "Suppliers retrieved successfully", suppliers));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
		SupplierResponse response = supplierService.getSupplierById(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Supplier retrieved successfully", response));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@RequestBody SupplierRequest request) {
		SupplierResponse response = supplierService.createSupplier(request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Supplier created successfully", response));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequest request) {
		SupplierResponse response = supplierService.updateSupplier(id, request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Supplier updated successfully", response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
		supplierService.deleteSupplier(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Supplier deleted successfully", null));
	}
}
