// === FUNCIONES GLOBALES ===
function regresar() {
    window.location.href = '/ventas';
}


function updateComportamiento(selectElement){
    var $select = $('#mov');
    var $comp = $('#comportamiento');
    if ($select.length === 0) return;
    var id = $select.val();
    if (!id) { if ($comp.length) $comp.val(''); return; }

    fetch(`/tipos-documento/find/${id}`)
           .then(response => {
               if (!response.ok) {
                   throw new Error(`Error HTTP: ${response.status}`);
               }
               return response.json();
           })
           .then(dto => {

                let comportamiento= document.getElementById('comportamiento');
                if(comportamiento){
                    comportamiento.value= dto.comportamiento.codigo || '';
                }
               console.log('✅ Documento recibido:', dto);


           })
           .catch(error => {
               console.error('❌ Error:', error);
               alert('Error al obtener el documento');
           });
}


function formatMoney(amount) {
    return '$' + amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
}

// Esperar a que jQuery esté disponible
(function() {
    // Si no estamos en una página de ventas, no ejecutar nada
    if (!document.location.pathname.includes('/ventas')) {
        return;
    }

    function initVentasPage() {
        // Verificar que jQuery esté disponible
        if (typeof $ === 'undefined') {
            setTimeout(initVentasPage, 100);
            return;
        }

        // Inicializar DataTables
        if ($('.table').not('.no-datatable').length > 0) {
            $('.table').not('.no-datatable').each(function() {
                try {
                    if (!$.fn.DataTable.isDataTable(this)) {
                        $(this).DataTable({
                            responsive: true,
                            pageLength: 10,
                            language: {
                                search: "Buscar:",
                                lengthMenu: "Mostrar _MENU_ entradas",
                                info: "Mostrando _START_ a _END_ de _TOTAL_ entradas",
                                infoEmpty: "No hay entradas para mostrar",
                                infoFiltered: "(filtrado de _MAX_ entradas totales)",
                                paginate: {
                                    first: "Primero",
                                    last: "Último",
                                    next: "Siguiente",
                                    previous: "Anterior"
                                },
                                zeroRecords: "No se encontraron registros coincidentes"
                            }
                        });
                    }
                } catch (e) {
                    console.warn('Error al inicializar DataTable en una tabla:', e);
                }
            });
        }

        // Manejar el modal de productos si existe
        if ($('#productosModal').length > 0) {
            $('#productosModal').on('shown.bs.modal', function() {
                $('#searchProduct').focus();
            });
        }

        // Formatear campos de dinero
        $('.money').each(function() {
            const value = parseFloat($(this).text().replace('$', '').replace(',', ''));
            if (!isNaN(value)) {
                $(this).text(new Intl.NumberFormat('es-ES', {
                    style: 'currency',
                    currency: 'USD'
                }).format(value));
            }
        });

        // Validar formulario de venta
        $('form').submit(function(e) {
            const detalles = $('#detalleTable tbody tr').length;
            if (detalles === 0) {
                e.preventDefault();
                alert('Debe agregar al menos un producto al detalle de la venta');
                if ($('#productosModal').length > 0) {
                    $('#productosModal').modal('show');
                }
            }
        });
    }

    // Inicializar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initVentasPage);
    } else {
        initVentasPage();
    }
})();

// Funciones y handlers específicos del formulario de ventas
(function() {
    // Ejecutar solo en páginas de ventas
    if (!document.location.pathname.includes('/ventas')) return;

    function ventasInit() {
        if (typeof $ === 'undefined') {
            setTimeout(ventasInit, 100);
            return;
        }

        // Búsqueda local en el modal de productos
        $('#searchProduct').on('keyup', function() {
            const term = $(this).val().toLowerCase();
            $('#productosTableModal tbody tr').each(function() {
                const text = $(this).text().toLowerCase();
                $(this).toggle(text.indexOf(term) > -1);
            });
        });

        // Agregar producto al detalle
        $(document).on('click', '.agregar-producto', function() {
            const id = $(this).data('id');
            const codigo = $(this).data('codigo');
            const nombre = $(this).data('nombre');
            const precio = parseFloat($(this).data('precio')) || 0;

            if (!id) return;

            // Evitar duplicados
            if ($('#detalleTable tbody tr[data-id="' + id + '"]').length > 0) {
                if (typeof showToast === 'function') {
                    showToast('Este producto ya está en el detalle', 'warning');
                } else {
                    alert('Este producto ya está en el detalle');
                }
                return;
            }

            const row = `\n                <tr data-id="${id}">\n                    <td>${nombre} <small class="text-muted">(${codigo})</small></td>\n                    <td>\n                        <input type="number" class="form-control cantidad" value="1" min="1" style="width: 80px;">\n                    </td>\n                    <td class="precio-unitario">${formatMoney(precio)}</td>\n                    <td class="subtotal">${formatMoney(precio)}</td>\n                    <td>\n                        <button type="button" class="btn btn-sm btn-danger eliminar-producto">\n                            <i class="fas fa-trash"></i>\n                        </button>\n                    </td>\n                </tr>\n            `;

            $('#detalleTable tbody').append(row);
            $('#detalleTable tbody .dataTables_empty').remove();
            calcularTotal();

            // Aplicar deshabilitación si corresponde
            if (window.formularioDeshabilitado) {
                $('#detalleTable .eliminar-producto').last().prop('disabled', true).addClass('disabled');
                $('#detalleTable input').last().prop('disabled', true);
            }

            // Cerrar modal si está presente
            if ($('#productosModal').length) {
                $('#productosModal').modal('hide');
            }
        });

        // Eliminar producto
        $(document).on('click', '.eliminar-producto', function() {
            $(this).closest('tr').remove();
            calcularTotal();
        });

        // Cambiar cantidad
        $(document).on('change', '.cantidad', function() {
            const row = $(this).closest('tr');
            const precioText = row.find('.precio-unitario').text().replace('$', '').replace(/,/g, '');
            const precio = parseFloat(precioText) || 0;
            const cantidad = parseInt($(this).val()) || 1;
            const subtotal = precio * cantidad;
            row.find('.subtotal').text(formatMoney(subtotal));
            calcularTotal();
        });

        // Recalcular al cambiar descuento
        $(document).on('input change', '#descuento', function() {
            calcularTotal();
        });

        // Mostrar toast inicial si existe mensaje
        if (window.toastMessage) {
            if (typeof showToast === 'function') {
                showToast(window.toastMessage, window.toastType || 'success', 5000);
            } else {
                // fallback: crear un alert
                console.log('Toast:', window.toastMessage);
            }
        }

        // Si el formulario está deshabilitado, aplicar restricciones UI
        if (window.formularioDeshabilitado) {
            $('#agregarProductoBtn').prop('disabled', true).addClass('disabled');
            $('#detalleTable input').prop('disabled', true);
            $('#detalleTable .eliminar-producto').prop('disabled', true).addClass('disabled');
        }
    }

    function calcularTotal() {
        let subtotal = 0;
        $('#detalleTable tbody tr').each(function() {
            const subtotalText = $(this).find('.subtotal').text().replace('$', '').replace(/,/g, '');
            const subtotalItem = parseFloat(subtotalText) || 0;
            subtotal += subtotalItem;
        });
        const descuento = parseFloat($('#descuento').val()) || 0;
        const total = Math.max(0, subtotal - descuento);
        $('#totalVenta').text(formatMoney(total));
        $('#total').val(total.toFixed(2));
    }

    function prepararDetalles() {
        // Validar que haya al menos un detalle
        if ($('#detalleTable tbody tr').length === 0) {
            if (typeof showToast === 'function') showToast('Debe agregar al menos un producto a la venta', 'error');
            else alert('Debe agregar al menos un producto a la venta');
            return false;
        }

        const detallesContainer = $('#detallesHidden');
        detallesContainer.empty();
        let index = 0;
        $('#detalleTable tbody tr').each(function() {
            const productoId = $(this).data('id') || 0;
            const cantidad = $(this).find('.cantidad').val();
            const precioText = $(this).find('.precio-unitario').text().replace('$', '').replace(/,/g, '');
            const precioUnitario = parseFloat(precioText) || 0;
            const subtotalText = $(this).find('.subtotal').text().replace('$', '').replace(/,/g, '');
            const subtotal = parseFloat(subtotalText) || 0;

            detallesContainer.append(`<input type="hidden" name="detalles[${index}].producto.id" value="${productoId}">`);
            detallesContainer.append(`<input type="hidden" name="detalles[${index}].cantidad" value="${cantidad}">`);
            detallesContainer.append(`<input type="hidden" name="detalles[${index}].precioUnitario" value="${precioUnitario}">`);
            detallesContainer.append(`<input type="hidden" name="detalles[${index}].subtotal" value="${subtotal}">`);
            index++;
        });

        return true;
    }

    // Exponer prepararDetalles
    window.prepararDetalles = prepararDetalles;

    // Inicializar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', ventasInit);
    } else {
        ventasInit();
    }
})();