package com.uisrael.drinkhouse.aplicacion.servicios;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Proveedor;

public class FacturaIAService {

	private final IProveedorUseCase proveedorUseCase;
	
	private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public FacturaIAService(IProveedorUseCase proveedorUseCase) {
		this.proveedorUseCase = proveedorUseCase;
	}

	public Proveedor procesarProveedorDeFactura(ProveedorIADto datosProveedor, Integer negocioId) {
		validarDatosProveedorIA(datosProveedor);
		
		Proveedor proveedor = new Proveedor();
		proveedor.setNegocioId(negocioId);
		proveedor.setRuc(datosProveedor.getRuc());
		proveedor.setRazonSocial(datosProveedor.getRazonSocial());
		proveedor.setDireccion(datosProveedor.getDireccionMatriz());
		proveedor.setTelefono(datosProveedor.getTelefono());
		proveedor.setEmail(datosProveedor.getEmail());
		
		return proveedorUseCase.buscarOCrearPorRuc(proveedor);
	}
	
	public LocalDate convertirFechaISO(String fechaISO) {
		if (fechaISO == null || fechaISO.trim().isEmpty()) {
			throw new IllegalArgumentException("La fecha no puede estar vacía");
		}
		return LocalDate.parse(fechaISO, ISO_DATE_FORMATTER);
	}
	
	private void validarDatosProveedorIA(ProveedorIADto datos) {
		List<String> errores = new ArrayList<>();
		
		if (datos.getRuc() == null || datos.getRuc().trim().isEmpty()) {
			errores.add("RUC del proveedor es obligatorio");
		} else if (!datos.getRuc().matches("\\d{13}")) {
			errores.add("RUC debe tener exactamente 13 dígitos");
		}
		
		if (datos.getRazonSocial() == null || datos.getRazonSocial().trim().isEmpty()) {
			errores.add("Razón social del proveedor es obligatoria");
		}
		
		if (datos.getEmail() == null || datos.getEmail().trim().isEmpty()) {
			errores.add("Email del proveedor es obligatorio (puede ser placeholder)");
		}
		
		if (!errores.isEmpty()) {
			throw new IllegalArgumentException("Datos de proveedor inválidos: " + String.join(", ", errores));
		}
	}

public static class ProveedorIADto {
		private String razonSocial;
		private String ruc;
		private String direccionMatriz;
		private String direccionSucursal;
		private String contribuyenteEspecialNro;
		private String obligadoLlevarContabilidad;
		private String telefono;
		private String email;
		
		public String getRazonSocial() { return razonSocial; }
		public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
		
		public String getRuc() { return ruc; }
		public void setRuc(String ruc) { this.ruc = ruc; }
		
		public String getDireccionMatriz() { return direccionMatriz; }
		public void setDireccionMatriz(String direccionMatriz) { this.direccionMatriz = direccionMatriz; }
		
		public String getDireccionSucursal() { return direccionSucursal; }
		public void setDireccionSucursal(String direccionSucursal) { this.direccionSucursal = direccionSucursal; }
		
		public String getContribuyenteEspecialNro() { return contribuyenteEspecialNro; }
		public void setContribuyenteEspecialNro(String contribuyenteEspecialNro) { this.contribuyenteEspecialNro = contribuyenteEspecialNro; }
		
		public String getObligadoLlevarContabilidad() { return obligadoLlevarContabilidad; }
		public void setObligadoLlevarContabilidad(String obligadoLlevarContabilidad) { this.obligadoLlevarContabilidad = obligadoLlevarContabilidad; }
		
		public String getTelefono() { return telefono; }
		public void setTelefono(String telefono) { this.telefono = telefono; }
		
		public String getEmail() { return email; }
		public void setEmail(String email) { this.email = email; }
	}

public static class DocumentoIADto {
		private String numeroFactura;
		private String claveAcceso;
		private String fechaEmision;
		private String fechaHoraAutorizacion;
		private String ambiente;
		private String tipoEmision;
		
		public String getNumeroFactura() { return numeroFactura; }
		public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
		
		public String getClaveAcceso() { return claveAcceso; }
		public void setClaveAcceso(String claveAcceso) { this.claveAcceso = claveAcceso; }
		
		public String getFechaEmision() { return fechaEmision; }
		public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }
		
		public String getFechaHoraAutorizacion() { return fechaHoraAutorizacion; }
		public void setFechaHoraAutorizacion(String fechaHoraAutorizacion) { this.fechaHoraAutorizacion = fechaHoraAutorizacion; }
		
		public String getAmbiente() { return ambiente; }
		public void setAmbiente(String ambiente) { this.ambiente = ambiente; }
		
		public String getTipoEmision() { return tipoEmision; }
		public void setTipoEmision(String tipoEmision) { this.tipoEmision = tipoEmision; }
	}

public static class ProductoIADto {
		private String codigoPrincipal;
		private String codigoAuxiliar;
		private String descripcion;
		private Integer cantidad;
		private Double precioUnitario;
		private Double descuento;
		private Double precioTotal;
		
		public String getCodigoPrincipal() { return codigoPrincipal; }
		public void setCodigoPrincipal(String codigoPrincipal) { this.codigoPrincipal = codigoPrincipal; }
		
		public String getCodigoAuxiliar() { return codigoAuxiliar; }
		public void setCodigoAuxiliar(String codigoAuxiliar) { this.codigoAuxiliar = codigoAuxiliar; }
		
		public String getDescripcion() { return descripcion; }
		public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
		
		public Integer getCantidad() { return cantidad; }
		public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
		
		public Double getPrecioUnitario() { return precioUnitario; }
		public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
		
		public Double getDescuento() { return descuento; }
		public void setDescuento(Double descuento) { this.descuento = descuento; }
		
		public Double getPrecioTotal() { return precioTotal; }
		public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }
	}

public static class TotalesIADto {
		private Double subtotal15;
		private Double subtotal0;
		private Double subtotalNoObjetoIva;
		private Double subtotalExentoIva;
		private Double subtotalSinImpuestos;
		private Double totalDescuento;
		private Double iva15;
		private Double ice;
		private Double irbpnr;
		private Double propina;
		private Double valorTotal;
		
		public Double getSubtotal15() { return subtotal15; }
		public void setSubtotal15(Double subtotal15) { this.subtotal15 = subtotal15; }
		
		public Double getSubtotal0() { return subtotal0; }
		public void setSubtotal0(Double subtotal0) { this.subtotal0 = subtotal0; }
		
		public Double getSubtotalNoObjetoIva() { return subtotalNoObjetoIva; }
		public void setSubtotalNoObjetoIva(Double subtotalNoObjetoIva) { this.subtotalNoObjetoIva = subtotalNoObjetoIva; }
		
		public Double getSubtotalExentoIva() { return subtotalExentoIva; }
		public void setSubtotalExentoIva(Double subtotalExentoIva) { this.subtotalExentoIva = subtotalExentoIva; }
		
		public Double getSubtotalSinImpuestos() { return subtotalSinImpuestos; }
		public void setSubtotalSinImpuestos(Double subtotalSinImpuestos) { this.subtotalSinImpuestos = subtotalSinImpuestos; }
		
		public Double getTotalDescuento() { return totalDescuento; }
		public void setTotalDescuento(Double totalDescuento) { this.totalDescuento = totalDescuento; }
		
		public Double getIva15() { return iva15; }
		public void setIva15(Double iva15) { this.iva15 = iva15; }
		
		public Double getIce() { return ice; }
		public void setIce(Double ice) { this.ice = ice; }
		
		public Double getIrbpnr() { return irbpnr; }
		public void setIrbpnr(Double irbpnr) { this.irbpnr = irbpnr; }
		
		public Double getPropina() { return propina; }
		public void setPropina(Double propina) { this.propina = propina; }
		
		public Double getValorTotal() { return valorTotal; }
		public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
	}

public static class FacturaIADto {
		private ProveedorIADto proveedor;
		private boolean crearProveedorAutomaticamente;
		private DocumentoIADto documento;
		private List<ProductoIADto> productos;
		private TotalesIADto totales;
		private String validacionTotales;
		
		public ProveedorIADto getProveedor() { return proveedor; }
		public void setProveedor(ProveedorIADto proveedor) { this.proveedor = proveedor; }
		
		public boolean isCrearProveedorAutomaticamente() { return crearProveedorAutomaticamente; }
		public void setCrearProveedorAutomaticamente(boolean crearProveedorAutomaticamente) { this.crearProveedorAutomaticamente = crearProveedorAutomaticamente; }
		
		public DocumentoIADto getDocumento() { return documento; }
		public void setDocumento(DocumentoIADto documento) { this.documento = documento; }
		
		public List<ProductoIADto> getProductos() { return productos; }
		public void setProductos(List<ProductoIADto> productos) { this.productos = productos; }
		
		public TotalesIADto getTotales() { return totales; }
		public void setTotales(TotalesIADto totales) { this.totales = totales; }
		
		public String getValidacionTotales() { return validacionTotales; }
		public void setValidacionTotales(String validacionTotales) { this.validacionTotales = validacionTotales; }
	}
}
