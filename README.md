# MediAlert  

**MediAlert** es una aplicación Android sencilla diseñada para ayudar a las personas a recordar la toma de medicamentos en horarios programados.  

---

## Propósito  
Muchas personas olvidan tomar sus medicamentos a tiempo, lo que puede afectar su salud y la efectividad de sus tratamientos.  
**MediAlert** busca resolver este problema con recordatorios claros, notificaciones y un manejo simple de la información de cada medicamento.  

---

## Problemática que resuelve  
- Olvido de medicamentos en tratamientos crónicos.  
- Falta de recordatorios personalizados para cada paciente.  
- Necesidad de una interfaz simple para adultos y cuidadores.  

---

## Pantallas iniciales (Activities)  
1. Inicio (splash screen con logo).  
2. Pantalla Principal: lista de medicamentos con horarios.  
3. Agregar Medicamento: formulario (nombre, dosis, hora).  
4. Detalle Medicamento: muestra información y permite editar/eliminar.  

---

## Navegación entre pantallas  
- Inicio → Principal con un `Intent`.  
- Principal → Agregar Medicamento con `Intent` explícito.  
- Principal → Detalle Medicamento con `Intent.putExtra("medicamentoId", id)`.  
- Detalle → Editar/Eliminar → Principal con `Intent` de retorno.  

---

## Tecnologías aplicadas  

| Tecnología / Componente | Uso en el proyecto |
|-------------------------|---------------------|
| Java | Lógica de la app y control de Activities |
| Android SDK | API para pantallas, almacenamiento y notificaciones |
| Activities | Cada pantalla de la app |
| Intents | Navegación entre pantallas y paso de datos |
| SQLite / Room | Guardar medicamentos, dosis y horarios |
| SharedPreferences (opcional) | Configuración simple o datos ligeros |
| AlarmManager | Programar recordatorios |
| BroadcastReceiver | Escuchar alarmas y activar notificaciones |
| Service (Foreground Service) | Mantener recordatorios en segundo plano |
| NotificationManager | Mostrar recordatorios en la barra de notificaciones |
| GitHub | Control de versiones y repositorio remoto |

---

## Datos a manejar  
- Nombre del medicamento.  
- Dosis.  
- Hora programada.  
- Estado (pendiente / tomado).  

Los datos se almacenarán en **SQLite (Room)** o en **SharedPreferences** en la versión simple.  

---

## Riesgos / Desafíos  
1. Manejo correcto de notificaciones en segundo plano (dependiendo de la versión de Android).  
2. Consumo de batería al programar alarmas frecuentes.  
3. Hacer una interfaz clara y accesible para adultos mayores.  

---

## Hitos de avance  

- Semana 1:  
  - Crear Activities básicas.  
  - Implementar navegación con Intents.  

- Semana 2:  
  - Almacenamiento local con SQLite/Room o SharedPreferences.  
  - CRUD de medicamentos.  

- Semana 3:  
  - Notificaciones con AlarmManager + BroadcastReceiver.  
  - Ajustes de usabilidad.  

---
