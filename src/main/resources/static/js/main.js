// Sales System - JavaScript Utilities

/**
 * Inicializa DataTable con configuración estándar
 */
function initializeDataTable(selector, options = {}) {
    // Verificar si la tabla ya está inicializada y destruirla si es necesario
    if ($.fn.dataTable.isDataTable(selector)) {
        $(selector).DataTable().destroy();
    }
    
    const defaultOptions = {
        responsive: true,
        pageLength: 25,
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Todos"]],
        language: {
            decimal: "",
            emptyTable: "No hay datos disponibles en la tabla",
            info: "Mostrando _START_ a _END_ de _TOTAL_ entradas",
            infoEmpty: "Mostrando 0 a 0 de 0 entradas",
            infoFiltered: "(filtrado de _MAX_ entradas totales)",
            infoPostFix: "",
            thousands: ",",
            lengthMenu: "Mostrar _MENU_ entradas",
            loadingRecords: "Cargando...",
            processing: "Procesando...",
            search: "Buscar:",
            zeroRecords: "No se encontraron registros que coincidan con la búsqueda",
            paginate: {
                first: "Primero",
                last: "Último",
                next: "Siguiente",
                previous: "Anterior"
            },
            aria: {
                sortAscending: ": activar para ordenar la columna de manera ascendente",
                sortDescending: ": activar para ordenar la columna de manera descendente"
            }
        }
    };
    
    const mergedOptions = Object.assign({}, defaultOptions, options);
    return $(selector).DataTable(mergedOptions);
}

/**
 * Inicializa DataTable con preset específico
 */
function initializeDataTableWithPreset(selector, preset, customOptions = {}) {
    // Verificar si la tabla ya está inicializada y destruirla si es necesario
    if ($.fn.dataTable.isDataTable(selector)) {
        $(selector).DataTable().destroy();
    }
    
    let presetOptions = {};
    
    // Configuraciones por preset
    switch(preset) {
        case 'clientes':
            presetOptions = {
                order: [[0, 'asc']], // Ordenar por nombre
                columnDefs: [
                    { targets: [-1], orderable: false } // Deshabilitar ordenamiento en acciones
                ]
            };
            break;
            
        case 'productos':
            presetOptions = {
                order: [[1, 'asc']], // Ordenar por nombre
                columnDefs: [
                    { targets: [2], className: 'text-end' }, // Alinear precio a la derecha
                    { targets: [3], className: 'text-center' }, // Centrar stock
                    { targets: [-1], orderable: false } // Deshabilitar ordenamiento en acciones
                ]
            };
            break;
            
        case 'ventas':
            presetOptions = {
                order: [[0, 'desc']], // Ordenar por fecha descendente
                columnDefs: [
                    { targets: [3], className: 'text-end' }, // Alinear total a la derecha
                    { targets: [-1], orderable: false } // Deshabilitar ordenamiento en acciones
                ]
            };
            break;
            
        case 'movimientos':
            presetOptions = {
                order: [[0, 'desc']], // Ordenar por fecha descendente
                columnDefs: [
                    { targets: [3, 4, 5], className: 'text-center' }, // Centrar cantidades y stocks
                    { targets: [6], className: 'text-end' }, // Alinear costo a la derecha
                    { targets: [-1], orderable: false } // Deshabilitar ordenamiento en acciones
                ]
            };
            break;
            
        case 'usuarios':
            presetOptions = {
                order: [[1, 'asc']], // Ordenar por username
                columnDefs: [
                    { targets: [-1], orderable: false } // Deshabilitar ordenamiento en acciones
                ]
            };
            break;
            
        case 'dashboard':
            presetOptions = {
                pageLength: 10,
                order: [[0, 'desc']], // Ordenar por fecha descendente
                columnDefs: [
                    { targets: [-1, -2], orderable: false } // Deshabilitar ordenamiento en últimas columnas
                ]
            };
            break;
            
        default:
            presetOptions = {};
    }
    
    // Combinar preset con opciones personalizadas
    const finalOptions = Object.assign({}, presetOptions, customOptions);
    return initializeDataTable(selector, finalOptions);
}

// Función para mostrar toasts
function showToast(message, type = 'success', duration = 5000) {
    const toastId = 'toast-' + Date.now();
    const iconClass = getToastIcon(type);
    const bgClass = getToastBgClass(type);
    
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 350px;">
            <div class="d-flex">
                <div class="toast-body py-3 px-3">
                    <i class="${iconClass} me-2" style="font-size: 1.1em;"></i>
                    <strong>${message}</strong>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    
    // Verificar que el contenedor existe
    const toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        console.error('Toast container not found');
        alert(message); // Fallback a alert
        return;
    }
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    const toastElement = document.getElementById(toastId);
    
    if (!toastElement) {
        console.error('Toast element not created');
        alert(message); // Fallback a alert
        return;
    }
    
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: duration
    });
    
    toast.show();
    
    // Remover el toast del DOM después de que se oculte
    toastElement.addEventListener('hidden.bs.toast', function () {
        toastElement.remove();
    });
}

function getToastIcon(type) {
    switch(type) {
        case 'success': return 'fas fa-check-circle';
        case 'error': return 'fas fa-exclamation-circle';
        case 'warning': return 'fas fa-exclamation-triangle';
        case 'info': return 'fas fa-info-circle';
        default: return 'fas fa-info-circle';
    }
}

function getToastBgClass(type) {
    switch(type) {
        case 'success': return 'bg-success';
        case 'error': return 'bg-danger';
        case 'warning': return 'bg-warning';
        case 'info': return 'bg-info';
        default: return 'bg-primary';
    }
}

// Función para deshabilitar formulario
function disableFormElements(formSelector) {
    console.log('Deshabilitando elementos del formulario:', formSelector);
    
    // Deshabilitar inputs, selects y textareas
    $(formSelector + ' input').not('[type="hidden"]').prop('disabled', true);
    $(formSelector + ' select').prop('disabled', true);
    $(formSelector + ' textarea').prop('disabled', true);
    
    // Deshabilitar botones específicos (no todos para evitar conflictos)
    $(formSelector + ' button[type="submit"]').prop('disabled', true);
    $(formSelector + ' .btn-primary, ' + formSelector + ' .btn-success').addClass('disabled').prop('disabled', true);
    
    // Deshabilitar botones de eliminar del detalle
    $('#detalleTable .eliminar-producto').prop('disabled', true).addClass('disabled');
    $('#agregarProductoBtn').prop('disabled', true).addClass('disabled');
    
    console.log('Elementos deshabilitados incluyendo botones de detalle');
}

// Función para habilitar formulario
function enableFormElements(formSelector) {
    console.log('Habilitando elementos del formulario:', formSelector);
    
    $(formSelector + ' input').prop('disabled', false);
    $(formSelector + ' select').prop('disabled', false);
    $(formSelector + ' textarea').prop('disabled', false);
    $(formSelector + ' button').prop('disabled', false);
    $(formSelector + ' .btn').removeClass('disabled');
    
    // Habilitar botones de eliminar del detalle
    $('#detalleTable .eliminar-producto').prop('disabled', false).removeClass('disabled');
    $('#agregarProductoBtn').prop('disabled', false).removeClass('disabled');
    
    console.log('Elementos habilitados incluyendo botones de detalle');
}

// Funciones de utilidad adicionales
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'EUR'
    }).format(amount);
}

function formatDate(date) {
    return new Intl.DateTimeFormat('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).format(new Date(date));
}

function formatDateTime(date) {
    return new Intl.DateTimeFormat('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(new Date(date));
}

// Función para enviar formulario con AJAX
function enviarFormularioAjax(accion) {
    debugger;
    console.log('=== ENVIANDO FORMULARIO CON AJAX ===');
    console.log('Acción seleccionada:', accion);
    
    // Mostrar spinner
    var loadingMessage = accion === 'afectar' ? 'Afectando...' : 'Guardando...';
    mostrarSpinner(loadingMessage);
    
    // Preparar detalles antes del envío
    if (typeof prepararDetalles === 'function' && !prepararDetalles()) {
        ocultarSpinner();
        return false;
    }
    
    // Obtener datos del formulario
    var formData = new FormData(document.getElementById('ventaForm'));
    
    // Agregar explícitamente la acción
    formData.set('accion', accion);
    
    console.log('FormData preparada:');
    for (let [key, value] of formData.entries()) {
        console.log(key + ':', value);
    }
    
    // Determinar URL
    var url = $('#ventaForm').attr('action');
    console.log('URL destino:', url);
    
    // Enviar con AJAX
    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            console.log('=== RESPUESTA EXITOSA ===');
            ocultarSpinner();
            
            // Reemplazar el contenido de la página con la respuesta
            document.open();
            document.write(response);
            document.close();
        },
        error: function(xhr, status, error) {
            console.error('=== ERROR EN AJAX ===');
            console.error('Status:', status);
            console.error('Error:', error);
            console.error('Response:', xhr.responseText);
            
            ocultarSpinner();
            showToast('Error al procesar la solicitud: ' + error, 'error');
        }
    });
    
    return false; // Prevenir envío normal del formulario
}

// Función para mostrar spinner
function mostrarSpinner(mensaje) {
    console.log('Mostrando spinner:', mensaje);
    var spinner = $('#loadingOverlay');
    if (spinner.length) {
        spinner.find('.loading-text').text(mensaje || 'Procesando...');
        spinner.show();
    }
}

// Función para ocultar spinner
function ocultarSpinner() {
    console.log('Ocultando spinner');
    var spinner = $('#loadingOverlay');
    if (spinner.length) {
        spinner.hide();
    }
}

// Función para aplicar deshabilitación a los detalles de venta
function aplicarDeshabilitacionDetalles(formularioDeshabilitado) {
    console.log('Aplicando deshabilitación de detalles:', formularioDeshabilitado);
    
    if (formularioDeshabilitado) {
        // Deshabilitar campos principales
        $('#fecha, #cliente, #tipoDocumento, #condicionPago, #observaciones').prop('disabled', true);
        
        // Deshabilitar botones de acción
        $('#btnGuardar, #btnAfectar').prop('disabled', true);
        
        // Deshabilitar controles de productos
        $('#producto, #cantidad, #precio').prop('disabled', true);
        $('#btnAgregarProducto').prop('disabled', true);
        
        // Aplicar clase visual
        $('.form-control, .form-select').addClass('disabled-field');
        
        // Deshabilitar botones de eliminar en detalles
        $('.btn-eliminar-detalle, .eliminar-producto').prop('disabled', true).addClass('disabled');
        
        console.log("Formulario y detalles deshabilitados");
    } else {
        // Habilitar elementos
        $('#fecha, #cliente, #tipoDocumento, #condicionPago, #observaciones').prop('disabled', false);
        $('#btnGuardar, #btnAfectar').prop('disabled', false);
        $('#producto, #cantidad, #precio').prop('disabled', false);
        $('#btnAgregarProducto').prop('disabled', false);
        $('.form-control, .form-select').removeClass('disabled-field');
        $('.btn-eliminar-detalle, .eliminar-producto').prop('disabled', false).removeClass('disabled');
        
        console.log("Formulario y detalles habilitados");
    }
}

// Función de compatibilidad para mostrar toast
function mostrarToast(mensaje, tipo) {
    console.log('Mostrando toast:', mensaje, tipo);
    // Usar la función showToast que ya existe
    if (typeof showToast === 'function') {
        showToast(mensaje, tipo);
    } else {
        // Fallback a alert si showToast no está disponible
        alert(mensaje);
    }
}
