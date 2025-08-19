// producto.js
// Validación y lógica del formulario de productos

document.addEventListener('DOMContentLoaded', function() {
    // Referencias a elementos del DOM
    const form = document.getElementById('productoForm');
    const codigoInput = document.getElementById('codigo');
    const nombreInput = document.getElementById('nombre');
    const productoId = document.querySelector('input[name="id"]').value;
    const isNewProduct = !productoId;

    // Validación en tiempo real del código
    let codigoTimeout;
    codigoInput.addEventListener('input', function() {
        clearTimeout(codigoTimeout);
        codigoTimeout = setTimeout(() => {
            validateCodigo();
        }, 500);
    });

    function validateCodigo() {
        const codigo = codigoInput.value.trim();
        if (!codigo) return;
        const url = `/productos/validar-codigo?codigo=${encodeURIComponent(codigo)}` +
                   (productoId ? `&id=${productoId}` : '');
        fetch(url)
            .then(response => response.json())
            .then(data => {
                const codigoError = document.getElementById('codigoError');
                const codigoSuccess = document.getElementById('codigoSuccess');
                if (data.existe) {
                    codigoInput.classList.add('is-invalid');
                    codigoInput.classList.remove('is-valid');
                    codigoError.textContent = 'This code already exists. Please enter a different code.';
                    codigoError.style.display = 'block';
                    codigoSuccess.style.display = 'none';
                } else {
                    codigoInput.classList.remove('is-invalid');
                    codigoInput.classList.add('is-valid');
                    codigoError.style.display = 'none';
                    codigoSuccess.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error validating codigo:', error);
            });
    }

    // Validación del formulario
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();
        if (validateForm()) {
            if (codigoInput.classList.contains('is-invalid')) {
                alert('Please correct the errors before saving.');
                return;
            }
            form.submit();
        }
        form.classList.add('was-validated');
    });

    function validateForm() {
        let isValid = true;
        // Validar código
        const codigo = document.getElementById('codigo');
        if (!codigo.value.trim()) {
            codigo.classList.add('is-invalid');
            isValid = false;
        } else {
            codigo.classList.remove('is-invalid');
        }
        // Validar nombre
        const nombre = document.getElementById('nombre');
        if (!nombre.value.trim()) {
            nombre.classList.add('is-invalid');
            isValid = false;
        } else {
            nombre.classList.remove('is-invalid');
        }
        // Validar categoría
        const categoria = document.getElementById('categoriaId');
        if (!categoria.value) {
            categoria.classList.add('is-invalid');
            isValid = false;
        } else {
            categoria.classList.remove('is-invalid');
        }
        // Validar precio
        const precio = document.getElementById('precio');
        if (!precio.value || parseFloat(precio.value) <= 0) {
            precio.classList.add('is-invalid');
            isValid = false;
        } else {
            precio.classList.remove('is-invalid');
        }
        // Validar unidad
        const unidad = document.getElementById('unidad');
        if (!unidad.value) {
            unidad.classList.add('is-invalid');
            isValid = false;
        } else {
            unidad.classList.remove('is-invalid');
        }
        return isValid;
    }

    // Auto-generar código si está vacío (solo para productos nuevos)
    if (isNewProduct && !codigoInput.value) {
        nombreInput.addEventListener('blur', function() {
            if (!codigoInput.value && nombreInput.value) {
                const codigo = generateProductCode(nombreInput.value);
                codigoInput.value = codigo;
                validateCodigo();
            }
        });
    }

    function generateProductCode(nombre) {
        const prefix = nombre.substring(0, 3).toUpperCase();
        const suffix = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
        return prefix + suffix;
    }

    // Establecer fecha actual si está vacía (solo para productos nuevos)
    const fechaAltaInput = document.getElementById('fechaAlta');
    if (fechaAltaInput && !fechaAltaInput.value && isNewProduct) {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        fechaAltaInput.value = `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    // Establecer estatus por defecto
    const estatusSelect = document.getElementById('estatus');
    if (estatusSelect && !estatusSelect.value && isNewProduct) {
        estatusSelect.value = 'ACTIVO';
    }

    // Sugerir unidad basada en el tipo de producto
    const tipoSelect = document.getElementById('tipo');
    const unidadSelect = document.getElementById('unidad');
    if (tipoSelect && unidadSelect) {
        tipoSelect.addEventListener('change', function() {
            const tipo = this.value;
            let sugerencia = '';
            switch(tipo) {
                case 'NORMAL': sugerencia = 'PIEZA'; break;
                case 'SERIE': sugerencia = 'UNIDAD'; break;
                case 'MATERIA_PRIMA': sugerencia = 'KG'; break;
                case 'PRODUCTO_TERMINADO': sugerencia = 'PIEZA'; break;
                case 'INSUMO': sugerencia = 'UNIDAD'; break;
                case 'HERRAMIENTA': sugerencia = 'PIEZA'; break;
                case 'EQUIPO': sugerencia = 'UNIDAD'; break;
                case 'CONSUMIBLE': sugerencia = 'PAQUETE'; break;
                case 'SERVICIO': sugerencia = 'PIEZA'; break;
                default: sugerencia = 'UNIDAD';
            }
            if (sugerencia && !unidadSelect.value) {
                unidadSelect.value = sugerencia;
                const notification = document.createElement('div');
                notification.className = 'alert alert-info alert-dismissible fade show mt-2';
                notification.innerHTML = `
                    <i class="fas fa-info-circle me-2"></i>
                    Unidad sugerida: <strong>${sugerencia}</strong> para tipo <strong>${tipo.replace('_', ' ')}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                unidadSelect.parentNode.appendChild(notification);
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.remove();
                    }
                }, 3000);
            }
        });
    }

    // Vista previa de imagen subida
    document.getElementById('imagen').addEventListener('change', function(event) {
        const preview = document.getElementById('preview');
        preview.innerHTML = '';
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.innerHTML = `<img src='${e.target.result}' class='img-thumbnail' style='max-width:200px; max-height:200px;'>`;
            };
            reader.readAsDataURL(file);
        }
    });
});

function enviarFormularioAjax() {
    var btn = document.getElementById('btnGuardarProducto');
    var form = document.getElementById('productoForm');
    if (!form) {
        alert('No se encontró el formulario con id productoForm');
        console.error('No se encontró el formulario con id productoForm');
        btn.disabled = false;
        hideLoadingOverlay();
        return;
    }
    if (!validateForm()) {
        alert('Por favor, completa todos los campos obligatorios correctamente.');
        form.classList.add('was-validated');
        btn.disabled = false;
        hideLoadingOverlay();
        return;
    }
    var formData = new FormData(form);
    btn.disabled = true;
    showLoadingOverlay();
    fetch(form.getAttribute('action'), {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error('Error al guardar el producto');
        return response.text();
    })
    .then(html => {
        window.location.href = '/productos';
    })
    .catch(error => {
        alert(error.message);
    })
    .finally(() => {
        btn.disabled = false;
        hideLoadingOverlay();
    });
}

function cancelarProducto() {
    window.location.href = '/productos';
}

function nuevoProducto() {
    var form = document.getElementById('productoForm');
    if (!form) return;
    form.reset();
    var inputs = form.querySelectorAll('.is-valid, .is-invalid');
    inputs.forEach(function(input) {
        input.classList.remove('is-valid', 'is-invalid');
    });
    var codigoError = document.getElementById('codigoError');
    var codigoSuccess = document.getElementById('codigoSuccess');
    if (codigoError) codigoError.style.display = 'none';
    if (codigoSuccess) codigoSuccess.style.display = 'none';
    var preview = document.getElementById('preview');
    if (preview) preview.innerHTML = '';
    var fechaAltaInput = document.getElementById('fechaAlta');
    if (fechaAltaInput) {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        fechaAltaInput.value = `${year}-${month}-${day}T${hours}:${minutes}`;
    }
    var estatusSelect = document.getElementById('estatus');
    if (estatusSelect) estatusSelect.value = 'ACTIVO';
    var titulo = document.querySelector('h2.h3');
    if (titulo) titulo.textContent = 'New Product';
}

function showLoadingOverlay() {
    document.getElementById('loadingOverlay').style.display = 'block';
}
function hideLoadingOverlay() {
    document.getElementById('loadingOverlay').style.display = 'none';
}

