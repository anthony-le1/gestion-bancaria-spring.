async function login() {
    const user = document.getElementById('user').value;
    const pass = document.getElementById('pass').value;

    const res = await fetch('/api/login', {
        method: 'POST',
        body: JSON.stringify({user, pass})
    });
    const data = await res.json();

    // Guardamos la sesión en el navegador
    localStorage.setItem('usuario', JSON.stringify(data));

    // Redirección por Rol
    if(data.rol === 'ADMIN') window.location.href = 'dashboard-admin.html';
    else window.location.href = 'dashboard-cliente.html';
}