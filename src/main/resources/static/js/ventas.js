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

        // Inicializar DataTables si existe la tabla
        if ($('.table').length > 0) {
            $('.table').DataTable({
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

function formatMoney(amount) {
    return '$' + amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
}