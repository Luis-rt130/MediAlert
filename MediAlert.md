MediAlert












Instituto: Santo Tomas
APP: MediAlert
Integrantes: Luis Romero y Martin Rodriguez
Docente: Giovani Cáceres
Fecha: 15/10/2025
 
Introducción

El propósito de este proyecto es desarrollar una aplicación móvil sencilla e intuitiva, especialmente pensada para personas de edad avanzada que suelen tener dificultades para recordar los horarios de sus medicamentos. Muchas veces, la falta de recordatorios adecuados puede provocar que los pacientes olviden tomar sus dosis a tiempo, afectando la efectividad de sus tratamientos y, en algunos casos, su salud general. Por ello, nos propusimos crear una herramienta que contribuya a mejorar su calidad de vida y facilite el seguimiento de sus rutinas médicas.
La aplicación desarrollada se llama MediAlert, y fue diseñada para el sistema operativo Android. Su función principal es enviar recordatorios automáticos para la toma de medicamentos, permitiendo al usuario registrar el nombre del medicamento, la dosis correspondiente y la hora exacta en que debe tomarlo. Además, ofrece una interfaz clara, accesible y de fácil navegación, pensando en quienes no están familiarizados con la tecnología. Aunque está enfocada en adultos mayores, puede ser utilizada por cualquier persona que necesite gestionar su medicación de manera más organizada.
El presente informe tiene como propósito describir el proceso de desarrollo de la aplicación MediAlert, detallando sus características, componentes técnicos y decisiones de diseño adoptadas. A lo largo del trabajo se evaluó la funcionalidad de las pantallas, la efectividad de los recordatorios y la facilidad de uso de la interfaz. El objetivo principal fue comprobar que la aplicación cumple con los criterios de usabilidad, accesibilidad y utilidad, demostrando que puede ser una herramienta práctica para apoyar a los usuarios en su cuidado personal y en el cumplimiento de sus tratamientos médicos.

Descripción
La aplicación se llama MediAlert, y su objetivo principal es ayudar a los usuarios a recordar el momento exacto en que deben tomar sus medicamentos recetados, mediante recordatorios automáticos y notificaciones simples de configurar. Está pensada principalmente para adultos mayores o personas con tratamientos prolongados, aunque puede ser utilizada por cualquier usuario que necesite organizar su medicación diaria.
Entre sus funcionalidades principales, MediAlert permite:
•	Registrar medicamentos indicando su nombre, dosis y hora de toma.
•	Recibir alertas o notificaciones cuando llega el momento de tomar cada medicamento.
•	Editar o eliminar registros de forma rápida y sencilla.
•	Visualizar una lista ordenada de medicamentos pendientes o ya tomados, facilitando el control diario.
La aplicación fue desarrollada en Android Studio, utilizando Java como lenguaje principal de programación. Para la gestión de datos y recordatorios, se integraron tecnologías como Firebase (para el almacenamiento y sincronización de datos), en versiones locales, asegurando que el usuario pueda utilizar la app incluso sin conexión a internet.
En el informe se incluyen capturas de pantalla representativas de las principales pantallas del proyecto, como la vista de inicio, el listado de medicamentos y el formulario de registro, que muestran el diseño intuitivo y la estructura funcional de la aplicación.
Impacto
MediAlert tiene un impacto significativo en la vida de los usuarios, el ámbito de la salud y el uso de tecnología. La aplicación mejora la adherencia a los tratamientos al ayudar a tomar medicamentos correctamente y a tiempo, reduciendo olvidos, errores de dosis y riesgos médicos. Esto promueve autonomía, bienestar y un mejor control de la salud diaria, siendo especialmente útil para adultos mayores, pacientes con enfermedades crónicas y personas con rutinas complejas de medicación.
En el entorno clínico, facilita el seguimiento del tratamiento y mejora la comunicación entre pacientes, familiares y profesionales de la salud, ya que ofrece información clara y organizada. Su diseño moderno basado en Material Design 3 y el uso de GPS impulsa la digitalización de la salud, abriendo puertas a funciones futuras como recordatorios por ubicación o análisis más completos de hábitos.
A nivel social, contribuye a una cultura de autocuidado, reduce la carga en sistemas de salud al disminuir complicaciones por mal manejo de medicamentos, y promueve la inclusión para personas con dificultades de memoria o dependencia. Desde lo profesional, constituye un proyecto sólido, escalable y de alto valor formativo que puede evolucionar con nuevas funcionalidades como notificaciones avanzadas, base de datos local, historial y sincronización en la nube.
En conjunto, MediAlert representa una herramienta tecnológica accesible, ética y útil que fortalece el bienestar personal y facilita una gestión más responsable y eficiente de la salud.



OBS
El objetivo de MediAlert es ofrecer una aplicación móvil que permita a los usuarios gestionar de forma simple y segura sus medicamentos, organizando dosis, horarios y recordatorios a través de una interfaz moderna y fácil de usar. El sistema busca facilitar el autocuidado mediante funciones como autenticación básica, registro de medicamentos, visualización de próximas dosis y una herramienta de ubicación GPS para ampliar las posibilidades del seguimiento. Además, promueve una experiencia fluida con diseño Material Design 3, navegación intuitiva y una estructura preparada para futuras mejoras como notificaciones, base de datos local y sincronización.
Caso de uso
Propósito
Permitir que el usuario cree y mantenga un plan de medicación con recordatorios y registre las tomas realizadas.
Alcance
App móvil MediAlert. Afecta users, prescriptions, medicines, reminders, dose_logs.
Precondiciones
•	El usuario está autenticado (users.id disponible).
•	La app tiene conectividad para sincronizar (si aplica).
Postcondiciones
•	Plan y medicamentos guardados o actualizados.
•	Recordatorios programados según la pauta.
•	Historial de tomas disponible.
Flujo básico (éxito)
1.	Crear prescripción: El usuario crea una prescription con título, fechas y notas.
2.	Añadir medicamento: El usuario añade un medicine asociado a la prescripción (o directo al usuario), indicando name, dose, frequency y horario (time).
3.	Configurar recordatorios: El usuario define uno o más reminders con next_run_at, repeat_rule y timezone.
4.	Guardar: El sistema valida datos y persiste:
•	prescriptions.user_id = users.id
•	medicines.user_id = users.id y opcional prescription_id
•	reminders.medicine_id = medicines.id
5.	Programación: El sistema agenda notificaciones/alarms futuras en base a reminders.
6.	Registrar toma: Al recibir un recordatorio, el usuario marca la toma como realizada. El sistema crea un dose_logs con status = 'taken' y taken_at.
7.	Visualización: El usuario ve listado de medicamentos activos, próximos recordatorios y el historial de tomas.
Flujos alternos
•	A1. Omitir prescripción:
•	El usuario crea medicine sin prescription_id. El sistema guarda igual y programa reminders.
•	A2. Editar medicamento/recordatorio:
•	El usuario modifica medicine o reminders. El sistema actualiza registros y reprograma notificaciones.
•	A3. Marcar como inactivo:
•	El usuario desactiva un medicine (is_active = false). El sistema cancela recordatorios asociados.
•	A4. Toma perdida:
•	El usuario indica que no tomó el medicamento. El sistema crea dose_logs con status = 'missed'.
Reglas de negocio
•	RB1. Solo medicines.is_active = true se listan en la vista principal e intervienen en recordatorios.
•	RB2. reminders deshabilitados (is_enabled = false) no se programan.
•	RB3. Los dose_logs son inmutables; correcciones se registran como nuevo log con notas.
Requisitos no funcionales
•	RNF1. Persistencia consistente y orden de consulta por created_at DESC.
•	RNF2. Notificaciones fiables según timezone.
•	RNF3. Privacidad: datos ligados a users.id y no visibles entre usuarios.
Trazabilidad con entidades
•	Crear prescripción → prescriptions
•	Añadir medicamento → medicines (FK a users, opcional a prescriptions)
•	Recordatorios → reminders (FK a medicines)
•	Registro de toma → dose_logs (FK a medicines)

 
Diagrama de clase
 

Conclusión	
El desarrollo de MediAlert fue una experiencia valiosa que permitió aplicar conocimientos en programación, diseño y pruebas dentro del entorno de Android Studio. Entre las principales dificultades se destacó el manejo de notificaciones en segundo plano y la creación de una interfaz simple y accesible para adultos mayores.
Durante el proceso se aprendió sobre el uso de Integración con Firebase, además de la importancia de realizar pruebas constantes para asegurar un funcionamiento estable.
En general, el resultado final fue satisfactorio: la aplicación cumple con su propósito de ayudar a las personas a recordar sus medicamentos de manera práctica y confiable, demostrando un buen equilibrio entre funcionalidad y facilidad de uso.
