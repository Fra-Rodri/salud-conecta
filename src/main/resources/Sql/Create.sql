-- Crear esquema si no existe
--CREATE SCHEMA IF NOT EXISTS clinica;


-- Trigger para actualizar el campo fecha modificación
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
  NEW.fecha_modificacion := CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Enumeradores para roles y estados
CREATE TYPE rol_usuario AS ENUM ('admin', 'profesional', 'recepcion');


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
    estado VARCHAR(20) DEFAULT 'pendiente',
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

