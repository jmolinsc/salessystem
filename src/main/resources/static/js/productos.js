document.addEventListener('DOMContentLoaded', function() {
debugger;
    var btn = document.getElementById('btnGuardarProducto');
    var form = document.getElementById('productoForm');
    if (btn && form) {
        btn.onclick = function(e) {
            e.preventDefault();
            btn.disabled = true;
            var formData = new FormData(form);
            fetch(form.getAttribute('action'), {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) throw new Error('Error al guardar el producto');
                return response.text();
            })
            .then(html => {
                // Mostrar mensaje de éxito y actualizar los datos del formulario
                mostrarMensajeExito('Producto guardado correctamente');
                // Actualizar los datos del formulario si el backend regresa HTML actualizado
                if (html) {
                    var contenedor = document.getElementById('formContainer');
                    if (contenedor) {
                        contenedor.innerHTML = html;
                    }
                }
            })
            .catch(error => {
                alert(error.message);
            })
            .finally(() => {
                btn.disabled = false;
            });
        }
    } else {
        console.error('No se encontró el formulario o el botón de guardar producto');
    }
});

function mostrarMensajeExito(mensaje) {
    var toast = document.createElement('div');
    toast.className = 'alert alert-success alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3';
    toast.style.zIndex = '9999';
    toast.innerHTML = `
        <i class='fas fa-check-circle me-2'></i> ${mensaje}
        <button type='button' class='btn-close' data-bs-dismiss='alert'></button>
    `;
    document.body.appendChild(toast);
    setTimeout(function() {
        if (toast.parentNode) toast.parentNode.removeChild(toast);
    }, 3000);
}
