// DataTables Dark Mode Global Functions for NickelFox Theme
// Funciones globales para manejar el modo oscuro en DataTables

/**
 * Aplica los estilos de modo oscuro a las DataTables
 */
function applyDataTablesDarkMode() {
    var isDarkMode = document.documentElement.getAttribute('data-theme') === 'dark';
    
    if (isDarkMode) {
        $('.dataTables_wrapper').addClass('dark-mode-applied');
    } else {
        $('.dataTables_wrapper').removeClass('dark-mode-applied');
    }
}

/**
 * Configuración base común para DataTables con soporte para modo oscuro - ULTRA COMPACTA
 */
function getBaseDataTableConfig(customConfig = {}) {
    const baseConfig = {
        responsive: true,
        pageLength: 25, // Más registros por página para aprovechar el espacio
        lengthMenu: [[15, 25, 50, 100, -1], ['15', '25', '50', '100', 'Todos']],
        dom: '<"row"<"col-sm-6"l><"col-sm-6"f>><"row"<"col-sm-12"tr>><"row"<"col-sm-5"i><"col-sm-7"p>>', // Compacto
        order: [[0, 'desc']],
        language: {
            search: 'Buscar:',
            lengthMenu: '_MENU_',
            info: '_START_-_END_ de _TOTAL_', // Más compacto
            infoEmpty: 'Sin registros',
            infoFiltered: '(de _MAX_)',
            paginate: {
                first: 'Primero',
                last: 'Último',
                next: 'Sig',
                previous: 'Ant'
            },
            zeroRecords: 'Sin resultados',
            emptyTable: 'Sin datos',
            loadingRecords: 'Cargando...',
            processing: 'Procesando...'
        },
        drawCallback: function() {
            applyDataTablesDarkMode();
        },
        stateSave: true,
        processing: false, // Desactivar para más velocidad
        autoWidth: false,
        compact: true, // Activar modo compacto
        className: 'table-sm', // Bootstrap tabla pequeña
        columnDefs: [
            { 
                targets: '_all',
                className: 'compact-cell' // Clase para celdas compactas
            }
        ]
    };

    // Merge con configuración personalizada
    return Object.assign({}, baseConfig, customConfig);
}

/**
 * Inicializa una DataTable con configuración estándar y soporte para modo oscuro
 * @param {string} tableSelector - Selector de la tabla
 * @param {object} customConfig - Configuración personalizada (opcional)
 * @param {string} emptyMessage - Mensaje personalizado cuando no hay datos
 */
function initializeDataTable(tableSelector, customConfig = {}, emptyMessage = null) {
    const $table = $(tableSelector);
    
    if (!$table.length) {
        console.warn('DataTable: No se encontró la tabla con selector:', tableSelector);
        return null;
    }

    // Destruir DataTable existente si existe
    if ($.fn.DataTable.isDataTable(tableSelector)) {
        $table.DataTable().destroy();
    }

    // Verificar si hay datos en la tabla
    const hasData = $table.find('tbody tr').length > 0 && !$table.find('tbody tr td[colspan]').length;
    
    let config;
    if (hasData) {
        // Configuración completa para tablas con datos
        config = getBaseDataTableConfig(customConfig);
    } else {
        // Configuración básica para tablas vacías
        config = {
            responsive: true,
            language: getBaseDataTableConfig().language,
            paging: false,
            searching: false,
            info: false,
            ordering: false
        };
        
        if (emptyMessage) {
            config.language.emptyTable = emptyMessage;
        }
    }

    try {
        const dataTable = $table.DataTable(config);
        
        // Aplicar estilos de modo oscuro iniciales
        applyDataTablesDarkMode();
        
        return dataTable;
    } catch (error) {
        console.error('Error inicializando DataTable:', error);
        return null;
    }
}

/**
 * Inicializa el observador de cambios de tema para todas las DataTables
 */
function initializeDataTableThemeObserver() {
    // Aplicar estilos iniciales
    applyDataTablesDarkMode();
    
    // Observer para cambios de tema
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'attributes' && mutation.attributeName === 'data-theme') {
                setTimeout(applyDataTablesDarkMode, 100);
            }
        });
    });
    
    observer.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['data-theme']
    });
    
    return observer;
}

/**
 * Configuraciones predefinidas para diferentes tipos de tablas - ULTRA COMPACTAS
 */
const DataTablePresets = {
    // Para listas de productos
    productos: {
        columnDefs: [
            { orderable: false, targets: [-1] },
            { className: 'text-center compact-cell', targets: [3, -1] },
            { className: 'text-end compact-cell', targets: [2] },
            { width: '15%', targets: [0] },
            { width: '10%', targets: [-1] }
        ],
        order: [[1, 'asc']],
        pageLength: 25
    },
    
    // Para tabla modal de productos (5 columnas: Código, Nombre, Precio, Stock, Acción)
    productosModal: {
        columnDefs: [
            { orderable: false, targets: [-1] },
            { className: 'text-center compact-cell', targets: [3, 4] }, // Stock y Acción
            { className: 'text-end compact-cell', targets: [2] }, // Precio
            { width: '15%', targets: [0] }, // Código
            { width: '10%', targets: [-1] } // Acción
        ],
        order: [[1, 'asc']],
        pageLength: 10
    },
    
    // Para listas de ventas
    ventas: {
        columnDefs: [
            { orderable: false, targets: [-1] },
            { className: 'text-center compact-cell', targets: [2, 3, -1] },
            { className: 'text-end compact-cell', targets: [3] },
            { width: '100px', targets: [0] },
            { width: '80px', targets: [3] },
            { width: '60px', targets: [-1] }
        ],
        order: [[0, 'desc']],
        pageLength: 25
    },
    
    // Para movimientos de inventario
    movimientos: {
        columnDefs: [
            { orderable: false, targets: [-1] },
            { className: 'text-center compact-cell', targets: [3, 4, 5, -1] },
            { className: 'text-end compact-cell', targets: [6] },
            { width: '90px', targets: [0] },
            { width: '80px', targets: [2] },
            { width: '60px', targets: [3, 4, 5] },
            { width: '80px', targets: [6] },
            { width: '50px', targets: [-1] }
        ],
        order: [[0, 'desc']],
        pageLength: 25
    },
    
    // Para clientes
    clientes: {
        columnDefs: [
            { orderable: false, targets: [-1] },
            { className: 'text-center compact-cell', targets: [2, -1] },
            { width: '15%', targets: [0] },
            { width: '10%', targets: [-1] }
        ],
        order: [[1, 'asc']],
        pageLength: 25
    },
    
    // Para usuarios
    usuarios: {
        columnDefs: [
            { orderable: false, targets: [-1] },
            { className: 'text-center compact-cell', targets: [2, 3, -1] },
            { width: '15%', targets: [0] },
            { width: '10%', targets: [-1] }
        ],
        order: [[1, 'asc']],
        pageLength: 25
    },
    
    // Para dashboards (tablas pequeñas)
    dashboard: {
        pageLength: 15,
        lengthMenu: [[10, 15, 25, -1], ['10', '15', '25', 'Todos']],
        order: [[0, 'desc']],
        columnDefs: [
            { className: 'compact-cell', targets: '_all' }
        ]
    }
};

/**
 * Función de conveniencia para inicializar DataTable con preset
 * @param {string} tableSelector - Selector de la tabla
 * @param {string} preset - Nombre del preset (productos, ventas, etc.)
 * @param {object} additionalConfig - Configuración adicional
 */
function initializeDataTableWithPreset(tableSelector, preset, additionalConfig = {}) {
    const presetConfig = DataTablePresets[preset] || {};
    const mergedConfig = Object.assign({}, presetConfig, additionalConfig);
    return initializeDataTable(tableSelector, mergedConfig);
}

// Inicializar automáticamente cuando el documento esté listo
$(document).ready(function() {
    // Solo inicializar el observer si no se ha hecho ya
    if (!window.dataTableThemeObserverInitialized) {
        initializeDataTableThemeObserver();
        window.dataTableThemeObserverInitialized = true;
    }
});
