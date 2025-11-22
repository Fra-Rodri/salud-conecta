-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS clinica;


-- Trigger para actualizar el campo fecha modificación
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
  NEW.fecha_modificacion := CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- BORRADO
DROP TABLE clinica.cita CASCADE;
DROP TABLE clinica.informe CASCADE;
DROP TABLE clinica.negocio CASCADE;
DROP TABLE clinica.paciente CASCADE;
DROP TABLE clinica.paciente_usuario CASCADE;
DROP TABLE clinica.usuario CASCADE;
DROP TYPE rol_usuario;
DROP TYPE estado_cita;


-- Enumeradores para roles y estados
CREATE TYPE rol_usuario AS ENUM ('admin', 'profesional', 'recepcion');
CREATE TYPE estado_cita AS ENUM ('pendiente', 'confirmada', 'cancelada');


-- Tabla negocio
CREATE TABLE clinica.negocio (
    id serial4 NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT,
    telefono VARCHAR(20),
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT negocio_pk PRIMARY KEY (id)
);

CREATE TRIGGER negocio_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.negocio
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();



-- Tabla usuario (profesionales)
CREATE TABLE clinica.usuario (
	id serial4 NOT NULL,
	nombre varchar(100) NOT NULL,
	email varchar(100) NOT NULL,
	password varchar(255) NOT NULL,
	rol rol_usuario NOT NULL,
	negocio_id int4, --  NOT NULL, NO ES NECESARIO QUE PERTENEZCA A UN NEGOCIO, PUEDE SER UN TRABAJADOR DE LA EMPRESA SIN MÁS
	fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT usuario_pk PRIMARY KEY (id),
	CONSTRAINT usuario_email_uk UNIQUE (email) --,
	--CONSTRAINT usuario_negocio_id_fk FOREIGN KEY (negocio_id) REFERENCES clinica.negocio(id) ON DELETE SET NULL -- Si se borra un negocio no se borra el usuario, pero se le pone la fk a null, de esta manera al crear el usuario le valida en que negocio trabaja
);

CREATE INDEX usuario_email_idx ON clinica.usuario (email);

CREATE TRIGGER usuario_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.usuario
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();



-- Tabla paciente
CREATE TABLE clinica.paciente (
    id serial4 NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    fecha_nacimiento DATE,
    alta boolean DEFAULT TRUE,
    -- negocio_id int4 NOT NULL, -- NO TIENE SENTIDO TENER UN PACIENTE ASOCIADO A UN NEGOCIO
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT paciente_pk PRIMARY KEY (id) --,
    --CONSTRAINT paciente_negocio_id_fk FOREIGN KEY (negocio_id) REFERENCES clinica.negocio(id) -- Si se elimina un negocio NO se elimina el paciente
);

CREATE INDEX paciente_nombre_idx ON clinica.paciente(nombre);

CREATE TRIGGER paciente_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.paciente
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();



-- Relación N:M entre pacientes y usuarios
CREATE TABLE clinica.paciente_usuario (
    paciente_id INTEGER,
    usuario_id INTEGER,
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT paciente_usuario_pk PRIMARY KEY (paciente_id, usuario_id),
    CONSTRAINT paciente_usuario_paciente_id FOREIGN KEY (paciente_id) REFERENCES clinica.paciente(id) ON DELETE CASCADE, -- Si se elimina un paciente se elimina su paciente_usuario
    CONSTRAINT paciente_usuario_usuario_id FOREIGN KEY (usuario_id) REFERENCES clinica.usuario(id) ON DELETE CASCADE -- Si se elimina un usuario se elimina su paciente_usuario
);

CREATE TRIGGER paciente_usuario_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.paciente_usuario
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();



-- Tabla cita
CREATE TABLE clinica.cita (
    id serial4 NOT NULL,
    paciente_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    fecha_cita TIMESTAMP NOT NULL,
    motivo TEXT,
    estado estado_cita NOT NULL DEFAULT 'pendiente',
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT cita_pk PRIMARY KEY (id),
    CONSTRAINT cita_paciente_id_fk FOREIGN KEY (paciente_id) REFERENCES clinica.paciente(id) ON DELETE CASCADE, -- Si se elimina un paciente, se eliminan automáticamente sus citas
    CONSTRAINT cita_usuario_id_fk FOREIGN KEY (usuario_id) REFERENCES clinica.usuario(id) ON DELETE CASCADE -- Si se elimina un usuario (profesional), también se eliminan sus citas
);

CREATE TRIGGER cita_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.cita
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();



-- Tabla informe clínico
CREATE TABLE clinica.informe (
    id serial4 NOT NULL,
    --cita_id INTEGER NOT NULL, --NO TIENE SENTIDO QUE UN INFORME SEA DE UNA CITA, MAS BIEN DEBERIA DE SER DE UN PACIENTE SIN MAS
    usuario_id INTEGER,
    nombre_usuario VARCHAR(100),
    paciente_id INTEGER,
    nombre_paciente VARCHAR(100),
    contenido TEXT NOT NULL,
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT informe_pk PRIMARY KEY (id),
   --CONSTRAINT informe_cita_id_fk FOREIGN KEY (cita_id) REFERENCES clinica.cita(id), -- Si se elimina una cita, no se eliminan automáticamente el informe
    CONSTRAINT informe_usuario_id_fk FOREIGN KEY (usuario_id) REFERENCES clinica.usuario(id) ON DELETE SET NULL, -- Si se elimina un usuario (profesional), no se eliminan los informes, pero su fk se pone a null, esto ayuda que el informe solo se pueda crear si el fk existe
    CONSTRAINT informe_paciente_id_fk FOREIGN KEY (paciente_id) REFERENCES clinica.paciente(id) ON DELETE SET NULL -- Si se elimina un paciente, no se eliminan sus informes, pero su fk se pone a null, esto ayuda que el informe solo se pueda crear si el fk existe
); -- De esta forma el historial de informes siempre será guardado al no ser que se borre explicitamente

CREATE TRIGGER informe_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.informe
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();


-- INSERTS
INSERT INTO clinica.negocio (nombre, direccion, telefono) VALUES
('Clínica Mediterráneo', 'Calle Mayor 12, Alicante', '965123456'),
('Salud Costa Blanca', 'Av. de la Estación 45, Elche', '966789012'),
('Centro Médico Valencia', 'Calle Colón 8, Valencia', '963456789'),
('Clínica San Vicente', 'Av. Ancha de Castelar 101, San Vicente', '965987654'),
('Clínica Levante', 'Calle San Fernando 33, Murcia', '968123456'),
('Salud Integral', 'Calle Pintor Sorolla 22, Castellón', '964321789'),
('Centro Médico Almería', 'Paseo de Almería 10, Almería', '950456789'),
('Clínica Granada Salud', 'Calle Recogidas 5, Granada', '958654321'),
('Clínica Sevilla Norte', 'Av. de la Palmera 77, Sevilla', '954789123'),
('Clínica Córdoba Centro', 'Calle Cruz Conde 18, Córdoba', '957321456'),
('Clínica Málaga Este', 'Calle Larios 15, Málaga', '952987654'),
('Clínica Cádiz Salud', 'Av. Andalucía 12, Cádiz', '956123789'),
('Clínica Vigo Salud', 'Calle Urzáiz 40, Vigo', '986456123'),
('Centro Médico Coruña', 'Ronda de Nelle 88, A Coruña', '981789456'),
('Clínica Oviedo Centro', 'Calle Uría 25, Oviedo', '985321654'),
('Clínica Santander Salud', 'Calle Burgos 10, Santander', '942654789'),
('Clínica Pamplona Norte', 'Av. Baja Navarra 14, Pamplona', '948123654'),
('Clínica Zaragoza Centro', 'Calle Alfonso I 9, Zaragoza', '976789321'),
('Clínica Bilbao Salud', 'Gran Vía 50, Bilbao', '944321987'),
('Clínica Donostia', 'Calle San Martín 3, San Sebastián', '943987321'),
('Clínica Huesca Salud', 'Calle Zaragoza 22, Huesca', '974123456'),
('Clínica Teruel Centro', 'Av. Sagunto 33, Teruel', '978654321'),
('Clínica Castellón Norte', 'Calle Herrero 18, Castellón', '964987654'),
('Clínica Elche Sur', 'Av. Universidad 12, Elche', '966321789'),
('Clínica Alicante Centro', 'Calle San Vicente 7, Alicante', '965456123'),
('Clínica Murcia Este', 'Av. Juan Carlos I 45, Murcia', '968789456'),
('Clínica Albacete Salud', 'Calle Mayor 10, Albacete', '967123789'),
('Clínica Cuenca Centro', 'Av. República Argentina 5, Cuenca', '969456321'),
('Clínica Toledo Norte', 'Calle Comercio 8, Toledo', '925789123'),
('Clínica Ciudad Real', 'Av. del Rey 22, Ciudad Real', '926321456'),
('Clínica Guadalajara Salud', 'Calle Amparo 15, Guadalajara', '949987654'),
('Clínica Segovia Centro', 'Av. Acueducto 9, Segovia', '921123789'),
('Clínica Ávila Salud', 'Calle San Segundo 3, Ávila', '920456987'),
('Clínica León Norte', 'Av. Ordoño II 11, León', '987654321'),
('Clínica Zamora Centro', 'Calle Santa Clara 6, Zamora', '980321654'),
('Clínica Salamanca Salud', 'Av. Mirat 20, Salamanca', '923789456'),
('Clínica Burgos Norte', 'Calle Vitoria 30, Burgos', '947123789'),
('Clínica Soria Centro', 'Av. Constitución 12, Soria', '975456321'),
('Clínica Logroño Salud', 'Calle Gran Vía 40, Logroño', '941789123'),
('Clínica Vitoria Centro', 'Av. Gasteiz 18, Vitoria', '945321456'),
('Clínica Oviedo Norte', 'Calle Campomanes 5, Oviedo', '985987654'),
('Clínica Gijón Salud', 'Av. Constitución 22, Gijón', '984123789'),
('Clínica Lugo Centro', 'Calle Reina 10, Lugo', '982456321'),
('Clínica Ourense Norte', 'Av. Buenos Aires 7, Ourense', '988789123'),
('Clínica Pontevedra Salud', 'Calle Michelena 15, Pontevedra', '986321456'),
('Clínica Ferrol Centro', 'Av. de Esteiro 9, Ferrol', '981987654'),
('Clínica Santiago Salud', 'Calle del Hórreo 20, Santiago', '981123789'),
('Clínica Tarragona Norte', 'Av. Catalunya 33, Tarragona', '977456321'),
('Clínica Lleida Centro', 'Calle Mayor 18, Lleida', '973789123'),
('Clínica Girona Salud', 'Av. Jaume I 12, Girona', '972321456'),
('Clínica Barcelona Este', 'Calle Aragón 50, Barcelona', '934987654');


INSERT INTO clinica.usuario (nombre, email, password, rol, negocio_id) VALUES
('Laura Martínez', 'laura.martinez@clinica.com', 'pass123', 'admin', 1),
('Carlos Gómez', 'carlos.gomez@clinica.com', 'pass123', 'profesional', 2),
('Ana Ruiz', 'ana.ruiz@clinica.com', 'pass123', 'recepcion', 3),
('Javier Torres', 'javier.torres@clinica.com', 'pass123', 'profesional', 4),
('Marta López', 'marta.lopez@clinica.com', 'pass123', 'recepcion', 5),
('Luis Sánchez', 'luis.sanchez@clinica.com', 'pass123', 'admin', 6),
('Isabel Fernández', 'isabel.fernandez@clinica.com', 'pass123', 'profesional', 7),
('Pedro Navarro', 'pedro.navarro@clinica.com', 'pass123', 'recepcion', 8),
('Sonia Romero', 'sonia.romero@clinica.com', 'pass123', 'profesional', 9),
('David Alonso', 'david.alonso@clinica.com', 'pass123', 'admin', 10),
('Cristina Pérez', 'cristina.perez@clinica.com', 'pass123', 'profesional', 11),
('Raúl Ortega', 'raul.ortega@clinica.com', 'pass123', 'recepcion', 12),
('Beatriz Molina', 'beatriz.molina@clinica.com', 'pass123', 'profesional', 13),
('Fernando Gil', 'fernando.gil@clinica.com', 'pass123', 'admin', 14),
('Patricia Ramos', 'patricia.ramos@clinica.com', 'pass123', 'recepcion', 15),
('Alberto Castro', 'alberto.castro@clinica.com', 'pass123', 'profesional', 16),
('Rocío Ibáñez', 'rocio.ibanez@clinica.com', 'pass123', 'admin', 17),
('Manuel Domínguez', 'manuel.dominguez@clinica.com', 'pass123', 'profesional', 18),
('Elena Vargas', 'elena.vargas@clinica.com', 'pass123', 'recepcion', 19),
('Andrés Muñoz', 'andres.munoz@clinica.com', 'pass123', 'profesional', 20),
('Clara Jiménez', 'clara.jimenez@clinica.com', 'pass123', 'admin', 21),
('Iván Romero', 'ivan.romero@clinica.com', 'pass123', 'profesional', 22),
('Nuria Torres', 'nuria.torres@clinica.com', 'pass123', 'recepcion', 23),
('Pablo Navarro', 'pablo.navarro@clinica.com', 'pass123', 'profesional', 24),
('Lucía Sánchez', 'lucia.sanchez@clinica.com', 'pass123', 'recepcion', 25),
('Miguel Fernández', 'miguel.fernandez@clinica.com', 'pass123', 'admin', 26),
('Silvia Gómez', 'silvia.gomez@clinica.com', 'pass123', 'profesional', 27),
('Jorge Ruiz', 'jorge.ruiz@clinica.com', 'pass123', 'recepcion', 28),
('Natalia López', 'natalia.lopez@clinica.com', 'pass123', 'profesional', 29),
('Óscar Martínez', 'oscar.martinez@clinica.com', 'pass123', 'admin', 30),
('Eva Gil', 'eva.gil@clinica.com', 'pass123', 'profesional', 31),
('Rubén Ramos', 'ruben.ramos@clinica.com', 'pass123', 'recepcion', 32),
('Celia Castro', 'celia.castro@clinica.com', 'pass123', 'profesional', 33),
('Tomás Ibáñez', 'tomas.ibanez@clinica.com', 'pass123', 'admin', 34),
('Lorena Domínguez', 'lorena.dominguez@clinica.com', 'pass123', 'profesional', 35),
('Álvaro Vargas', 'alvaro.vargas@clinica.com', 'pass123', 'recepcion', 36),
('Paula Muñoz', 'paula.munoz@clinica.com', 'pass123', 'profesional', 37),
('Héctor Jiménez', 'hector.jimenez@clinica.com', 'pass123', 'admin', 38),
('Sandra Romero', 'sandra.romero@clinica.com', 'pass123', 'profesional', 39),
('Daniel Torres', 'daniel.torres@clinica.com', 'pass123', 'recepcion', 40),
('Marina Navarro', 'marina.navarro@clinica.com', 'pass123', 'profesional', 41),
('Víctor Sánchez', 'victor.sanchez@clinica.com', 'pass123', 'admin', 42),
('Raquel Fernández', 'raquel.fernandez@clinica.com', 'pass123', 'profesional', 43),
('Sergio Gómez', 'sergio.gomez@clinica.com', 'pass123', 'recepcion', 44),
('Patricia Ruiz', 'patricia.ruiz@clinica.com', 'pass123', 'profesional', 45),
('Ángel López', 'angel.lopez@clinica.com', 'pass123', 'admin', 46),
('Teresa Martínez', 'teresa.martinez@clinica.com', 'pass123', 'profesional', 47),
('Julián Gil', 'julian.gil@clinica.com', 'pass123', 'recepcion', 48),
('Mónica Ramos', 'monica.ramos@clinica.com', 'pass123', 'profesional', 49),
('Esteban Castro', 'esteban.castro@clinica.com', 'pass123', 'admin', 50);

INSERT INTO clinica.paciente (nombre, dni, fecha_nacimiento) VALUES
('María García', '12345678A', '1985-03-12'),
('José Rodríguez', '23456789B', '1978-07-25'),
('Lucía Fernández', '34567890C', '1992-11-05'),
('Antonio Martínez', '45678901D', '1980-01-30'),
('Carmen López', '56789012E', '1995-06-18'),
('Francisco Sánchez', '67890123F', '1975-09-09'),
('Laura Pérez', '78901234G', '1988-12-22'),
('Manuel Gómez', '89012345H', '1990-04-14'),
('Elena Ruiz', '90123456I', '1983-08-03'),
('Jorge Torres', '01234567J', '1979-02-17'),
('Sara Romero', '11223344K', '1993-05-29'),
('Luis Navarro', '22334455L', '1986-10-10'),
('Ana Alonso', '33445566M', '1991-03-08'),
('Raúl Ortega', '44556677N', '1982-07-01'),
('Beatriz Molina', '55667788O', '1987-11-11'),
('Fernando Gil', '66778899P', '1976-06-06'),
('Patricia Ramos', '77889900Q', '1994-09-19'),
('Alberto Castro', '88990011R', '1989-01-01'),
('Rocío Ibáñez', '99001122S', '1996-04-25'),
('Andrés Muñoz', '10111213T', '1981-12-12'),
('Clara Jiménez', '11121314U', '1984-07-07'),
('Iván Romero', '12131415V', '1977-03-03'),
('Nuria Torres', '13141516W', '1990-09-09'),
('Pablo Navarro', '14151617X', '1983-02-02'),
('Lucía Sánchez', '15161718Y', '1992-06-06'),
('Miguel Fernández', '16171819Z', '1979-11-11'),
('Silvia Gómez', '17181920A', '1985-08-08'),
('Jorge Ruiz', '18192021B', '1987-04-04'),
('Natalia López', '19202122C', '1991-10-10'),
('Óscar Martínez', '20212223D', '1980-12-12'),
('Eva Gil', '21222324E', '1986-01-01'),
('Rubén Ramos', '22232425F', '1993-03-03'),
('Celia Castro', '23242526G', '1984-05-05'),
('Tomás Ibáñez', '24252627H', '1978-06-06'),
('Lorena Domínguez', '25262728I', '1995-07-07'),
('Álvaro Vargas', '26272829J', '1982-08-08'),
('Paula Muñoz', '27282930K', '1990-09-09'),
('Héctor Jiménez', '28293031L', '1981-10-10'),
('Sandra Romero', '29303132M', '1983-11-11'),
('Daniel Torres', '30313233N', '1976-12-12'),
('Marina Navarro', '31323334O', '1989-01-01'),
('Víctor Sánchez', '32333435P', '1987-02-02'),
('Raquel Fernández', '33343536Q', '1994-03-03'),
('Sergio Gómez', '34353637R', '1985-04-04'),
('Patricia Ruiz', '35363738S', '1993-05-05'),
('Ángel López', '36373839T', '1981-06-06'),
('Teresa Martínez', '37383940U', '1986-07-07'),
('Julián Gil', '38394041V', '1979-08-08'),
('Mónica Ramos', '39404142W', '1992-09-09'),
('Esteban Castro', '40414243X', '1980-10-10');


INSERT INTO clinica.paciente_usuario (paciente_id, usuario_id) VALUES
(1, 2), (2, 3), (3, 4), (4, 5), (5, 6),
(6, 7), (7, 8), (8, 9), (9, 10), (10, 11),
(11, 12), (12, 13), (13, 14), (14, 15), (15, 16),
(16, 17), (17, 18), (18, 19), (19, 20), (20, 1),
(21, 22), (22, 23), (23, 24), (24, 25), (25, 26),
(26, 27), (27, 28), (28, 29), (29, 30), (30, 31),
(31, 32), (32, 33), (33, 34), (34, 35), (35, 36),
(36, 37), (37, 38), (38, 39), (39, 40), (40, 41),
(41, 42), (42, 43), (43, 44), (44, 45), (45, 46),
(46, 47), (47, 48), (48, 49), (49, 50), (50, 21);


INSERT INTO clinica.cita (paciente_id, usuario_id, fecha_cita, motivo, estado) VALUES
(1, 2, '2025-10-26 09:00:00', 'Consulta general', 'confirmada'),
(2, 3, '2025-10-26 10:00:00', 'Dolor de espalda', 'pendiente'),
(3, 4, '2025-10-26 11:00:00', 'Revisión anual', 'confirmada'),
(4, 5, '2025-10-26 12:00:00', 'Control de tensión', 'pendiente'),
(5, 6, '2025-10-26 13:00:00', 'Consulta dermatológica', 'cancelada'),
(6, 7, '2025-10-27 09:30:00', 'Chequeo preoperatorio', 'confirmada'),
(7, 8, '2025-10-27 10:30:00', 'Dolor abdominal', 'pendiente'),
(8, 9, '2025-10-27 11:30:00', 'Revisión ginecológica', 'confirmada'),
(9, 10, '2025-10-27 12:30:00', 'Consulta pediátrica', 'pendiente'),
(10, 11, '2025-10-27 13:30:00', 'Control de colesterol', 'confirmada'),
(11, 12, '2025-10-28 09:00:00', 'Consulta psicológica', 'pendiente'),
(12, 13, '2025-10-28 10:00:00', 'Revisión dental', 'cancelada'),
(13, 14, '2025-10-28 11:00:00', 'Consulta oftalmológica', 'confirmada'),
(14, 15, '2025-10-28 12:00:00', 'Dolor cervical', 'pendiente'),
(15, 16, '2025-10-28 13:00:00', 'Consulta nutricional', 'confirmada'),
(16, 17, '2025-10-29 09:30:00', 'Revisión postoperatoria', 'pendiente'),
(17, 18, '2025-10-29 10:30:00', 'Consulta cardiológica', 'confirmada'),
(18, 19, '2025-10-29 11:30:00', 'Dolor en rodilla', 'pendiente'),
(19, 20, '2025-10-29 12:30:00', 'Consulta respiratoria', 'confirmada'),
(20, 1, '2025-10-29 13:30:00', 'Control de glucosa', 'pendiente'),
(21, 22, '2025-10-30 09:00:00', 'Consulta general', 'confirmada'),
(22, 23, '2025-10-30 10:00:00', 'Dolor de espalda', 'pendiente'),
(23, 24, '2025-10-30 11:00:00', 'Revisión anual', 'confirmada'),
(24, 25, '2025-10-30 12:00:00', 'Control de tensión', 'pendiente'),
(25, 26, '2025-10-30 13:00:00', 'Consulta dermatológica', 'cancelada'),
(26, 27, '2025-10-31 09:30:00', 'Chequeo preoperatorio', 'confirmada'),
(27, 28, '2025-10-31 10:30:00', 'Dolor abdominal', 'pendiente'),
(28, 29, '2025-10-31 11:30:00', 'Revisión ginecológica', 'confirmada'),
(29, 30, '2025-10-31 12:30:00', 'Consulta pediátrica', 'pendiente'),
(30, 31, '2025-10-31 13:30:00', 'Control de colesterol', 'confirmada'),
(31, 32, '2025-11-01 09:00:00', 'Consulta psicológica', 'pendiente'),
(32, 33, '2025-11-01 10:00:00', 'Revisión dental', 'cancelada'),
(33, 34, '2025-11-01 11:00:00', 'Consulta oftalmológica', 'confirmada'),
(34, 35, '2025-11-01 12:00:00', 'Dolor cervical', 'pendiente'),
(35, 36, '2025-11-01 13:00:00', 'Consulta nutricional', 'confirmada'),
(36, 37, '2025-11-02 09:30:00', 'Revisión postoperatoria', 'pendiente'),
(37, 38, '2025-11-02 10:30:00', 'Consulta cardiológica', 'confirmada'),
(38, 39, '2025-11-02 11:30:00', 'Dolor en rodilla', 'pendiente'),
(39, 40, '2025-11-02 12:30:00', 'Consulta respiratoria', 'confirmada'),
(40, 41, '2025-11-02 13:30:00', 'Control de glucosa', 'pendiente'),
(41, 42, '2025-11-03 09:00:00', 'Consulta general', 'confirmada'),
(42, 43, '2025-11-03 10:00:00', 'Dolor de espalda', 'pendiente'),
(43, 44, '2025-11-03 11:00:00', 'Revisión anual', 'confirmada'),
(44, 45, '2025-11-03 12:00:00', 'Control de tensión', 'pendiente'),
(45, 46, '2025-11-03 13:00:00', 'Consulta dermatológica', 'cancelada'),
(46, 47, '2025-11-04 09:30:00', 'Chequeo preoperatorio', 'confirmada'),
(47, 48, '2025-11-04 10:30:00', 'Dolor abdominal', 'pendiente'),
(48, 49, '2025-11-04 11:30:00', 'Revisión ginecológica', 'confirmada'),
(49, 50, '2025-11-04 12:30:00', 'Consulta pediátrica', 'pendiente'),
(50, 21, '2025-11-04 13:30:00', 'Control de colesterol', 'confirmada');




INSERT INTO clinica.informe (usuario_id, nombre_usuario, paciente_id, nombre_paciente, contenido) VALUES
(2, 'Carlos Gómez', 1, 'María García', 'Paciente en buen estado general. Se recomienda seguimiento.'),
(3, 'Ana Ruiz', 2, 'José Rodríguez', 'Dolor lumbar persistente. Se prescribe fisioterapia.'),
(4, 'Javier Torres', 3, 'Lucía Fernández', 'Revisión sin hallazgos relevantes.'),
(5, 'Marta López', 4, 'Antonio Martínez', 'Tensión arterial controlada.'),
(6, 'Luis Sánchez', 5, 'Carmen López', 'Dermatitis leve. Se indica crema tópica.'),
(7, 'Isabel Fernández', 6, 'Francisco Sánchez', 'Preoperatorio completado. Apto para cirugía.'),
(8, 'Pedro Navarro', 7, 'Laura Pérez', 'Dolor abdominal remitido.'),
(9, 'Sonia Romero', 8, 'Manuel Gómez', 'Revisión ginecológica normal.'),
(10, 'David Alonso', 9, 'Elena Ruiz', 'Consulta pediátrica sin incidencias.'),
(11, 'Cristina Pérez', 10, 'Jorge Torres', 'Colesterol elevado. Se recomienda dieta.'),
(12, 'Raúl Ortega', 11, 'Sara Romero', 'Consulta psicológica inicial. Se agenda seguimiento.'),
(13, 'Beatriz Molina', 12, 'Luis Navarro', 'Revisión dental sin caries.'),
(14, 'Fernando Gil', 13, 'Ana Alonso', 'Oftalmología: visión estable.'),
(15, 'Patricia Ramos', 14, 'Raúl Ortega', 'Dolor cervical tratado con analgésico.'),
(16, 'Alberto Castro', 15, 'Beatriz Molina', 'Nutrición: se inicia plan personalizado.'),
(17, 'Rocío Ibáñez', 16, 'Fernando Gil', 'Postoperatorio sin complicaciones.'),
(18, 'Manuel Domínguez', 17, 'Patricia Ramos', 'Cardiología: ECG normal.'),
(19, 'Elena Vargas', 18, 'Alberto Castro', 'Rodilla inflamada. Se indica reposo.'),
(20, 'Andrés Muñoz', 19, 'Rocío Ibáñez', 'Consulta respiratoria: se descarta infección.'),
(1, 'Laura Martínez', 20, 'Andrés Muñoz', 'Glucosa controlada. Se mantiene tratamiento.'),
(22, 'Iván Romero', 21, 'Clara Jiménez', 'Paciente estable. Se recomienda control semestral.'),
(23, 'Nuria Torres', 22, 'Iván Romero', 'Dolor cervical leve. Se indica fisioterapia.'),
(24, 'Pablo Navarro', 23, 'Nuria Torres', 'Revisión ginecológica sin hallazgos.'),
(25, 'Lucía Sánchez', 24, 'Pablo Navarro', 'Consulta pediátrica. Niño sano.'),
(26, 'Miguel Fernández', 25, 'Lucía Sánchez', 'Colesterol normal. Se mantiene dieta.'),
(27, 'Silvia Gómez', 26, 'Miguel Fernández', 'Consulta psicológica. Se recomienda seguimiento.'),
(28, 'Jorge Ruiz', 27, 'Silvia Gómez', 'Revisión dental. Se detecta caries.'),
(29, 'Natalia López', 28, 'Jorge Ruiz', 'Oftalmología: miopía leve.'),
(30, 'Óscar Martínez', 29, 'Natalia López', 'Dolor cervical tratado con masaje.'),
(31, 'Eva Gil', 30, 'Óscar Martínez', 'Nutrición: se ajusta plan alimenticio.'),
(32, 'Rubén Ramos', 31, 'Eva Gil', 'Postoperatorio sin complicaciones.'),
(33, 'Celia Castro', 32, 'Rubén Ramos', 'Cardiología: presión arterial normal.'),
(34, 'Tomás Ibáñez', 33, 'Celia Castro', 'Rodilla inflamada. Se indica reposo.'),
(35, 'Lorena Domínguez', 34, 'Tomás Ibáñez', 'Consulta respiratoria: se descarta infección.'),
(36, 'Álvaro Vargas', 35, 'Lorena Domínguez', 'Glucosa controlada. Se mantiene tratamiento.'),
(37, 'Paula Muñoz', 36, 'Álvaro Vargas', 'Paciente estable. Se recomienda control anual.'),
(38, 'Héctor Jiménez', 37, 'Paula Muñoz', 'Dolor cervical leve. Se indica fisioterapia.'),
(39, 'Sandra Romero', 38, 'Héctor Jiménez', 'Revisión ginecológica sin hallazgos.'),
(40, 'Daniel Torres', 39, 'Sandra Romero', 'Consulta pediátrica. Niño sano.'),
(41, 'Marina Navarro', 40, 'Daniel Torres', 'Colesterol normal. Se mantiene dieta.'),
(42, 'Víctor Sánchez', 41, 'Marina Navarro', 'Consulta psicológica. Se recomienda seguimiento.'),
(43, 'Raquel Fernández', 42, 'Víctor Sánchez', 'Revisión dental. Sin incidencias.'),
(44, 'Sergio Gómez', 43, 'Raquel Fernández', 'Oftalmología: visión estable.'),
(45, 'Patricia Ruiz', 44, 'Sergio Gómez', 'Dolor cervical tratado con analgésico.'),
(46, 'Ángel López', 45, 'Patricia Ruiz', 'Nutrición: se inicia plan personalizado.'),
(47, 'Teresa Martínez', 46, 'Ángel López', 'Postoperatorio sin complicaciones.'),
(48, 'Julián Gil', 47, 'Teresa Martínez', 'Cardiología: ECG normal.'),
(49, 'Mónica Ramos', 48, 'Julián Gil', 'Rodilla inflamada. Se indica reposo.'),
(50, 'Esteban Castro', 49, 'Mónica Ramos', 'Consulta respiratoria: se descarta infección.');