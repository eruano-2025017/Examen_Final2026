-- Carga inicial de usuarios para auth-service
-- Contraseñas encriptadas con BCrypt:
-- admin@veterinaria.com -> admin123
-- veterinario@veterinaria.com -> vet123
-- cliente@correo.com -> cliente123

INSERT INTO usuarios (id, nombre, telefono, email, password, rol) VALUES
(1, 'Administrador General', '55512345', 'admin@veterinaria.com', '$2a$10$pjAXLQTrAfhJwKLcphKs5O8.NVnKDz32nIuqhHQK6ksH8cZFHYOOy', 'ADMIN'),
(2, 'Dr. Roberto Gomez', '55523456', 'veterinario@veterinaria.com', '$2a$10$tY9SkTNItHLSxGcWeW43qe.9WjVzzl/s340xSmAnKoAMIKygm4BfK', 'VET'),
(3, 'Juan Perez', '55534567', 'cliente@correo.com', '$2a$10$sa91IWoBcLR6MvKs4azXi.iTNfD4462YWkGat/N9VkKpbuBKnU5iG', 'CLIENTE');

