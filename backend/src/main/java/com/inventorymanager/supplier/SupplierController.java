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
		return null;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
		return null;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@RequestBody SupplierRequest request) {
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequest request) {
		return null;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
		return null;
	}
}
