-- Carga inicial de datos de prueba para citas-mascotas-service
-- Mascota asignada al Cliente con ID 3 (Juan Perez)
INSERT INTO mascotas (id, nombre, especie, raza, edad, cliente_id) VALUES
(1, 'Firulais', 'PERRO', 'Labrador Retriever', 3, 3);

-- Cita medica para la Mascota 1 con el Veterinario con ID 2 (Dr. Roberto Gomez)
INSERT INTO citas_medicas (id, mascota_id, veterinario_id, fecha_hora, motivo, estado) VALUES
(1, 1, 2, '2026-10-10 10:00:00', 'Chequeo general de rutina y vacunacion anual', 'PENDIENTE');

