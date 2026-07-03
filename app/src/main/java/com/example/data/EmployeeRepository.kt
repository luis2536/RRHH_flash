package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    fun getTasaBCVFlow(): Flow<Double> = employeeDao.getConfigFlow("tasa_bcv").map {
        it?.value?.toDoubleOrNull() ?: 39.45
    }

    suspend fun getTasaBCV(): Double {
        return employeeDao.getConfig("tasa_bcv")?.value?.toDoubleOrNull() ?: 39.45
    }

    suspend fun updateTasaBCV(newTasa: Double) {
        employeeDao.insertConfig(SystemConfig("tasa_bcv", newTasa.toString()))
    }

    suspend fun getEmployeeByCid(cid: String): Employee? {
        return employeeDao.getEmployeeByCid(cid)
    }

    fun getNoveltiesForEmployee(cid: String): Flow<List<Novelty>> {
        return employeeDao.getNoveltiesForEmployee(cid)
    }

    suspend fun insertEmployee(employee: Employee): Long {
        return employeeDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.deleteEmployee(employee)
    }

    suspend fun insertNovelty(novelty: Novelty) {
        employeeDao.insertNovelty(novelty)
    }

    suspend fun seedIfNeeded() {
        val currentEmployees = employeeDao.getAllEmployees().firstOrNull()
        if (currentEmployees.isNullOrEmpty()) {
            val seedList = listOf(
                Employee(1, "Alejandro", "García", "101", "0414-1111111", "123", "Tecnología", "Software Architect", 2500.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(2, "María", "Mendoza", "102", "0414-2222222", "123", "Tecnología", "DevOps Engineer", 1800.0, "Rotación Mañana", "Activo", "Pagado", "Procesando"),
                Employee(3, "Carlos", "Rodríguez", "103", "0424-3333333", "123", "Tecnología", "Full-Stack Dev", 1500.0, "Rotación Tarde", "Vacaciones", "Pagado", "Procesando"),
                Employee(4, "Diana", "Sánchez", "104", "0412-4444444", "123", "Tecnología", "Cybersecurity Lead", 2200.0, "Rotación Noche", "Activo", "Pagado", "Procesando"),
                Employee(5, "Esteban", "Pérez", "105", "0416-5555555", "123", "Tecnología", "QA Engineer", 1100.0, "Rotación Mañana", "Activo", "Pagado", "Procesando"),
                Employee(6, "Fabiola", "Gómez", "106", "0424-6666666", "123", "Tecnología", "Data Scientist", 1700.0, "Bloque Mixto", "Próximas", "Pagado", "Procesando"),
                Employee(7, "Gabriel", "López", "107", "0414-7777777", "123", "Tecnología", "SysAdmin Core", 1300.0, "Rotación Noche", "Activo", "Pagado", "Procesando"),
                
                Employee(8, "Héctor", "Torres", "108", "0412-8888888", "123", "Finanzas", "Contralor General", 2000.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(9, "Irene", "Ramírez", "109", "0416-9999999", "123", "Finanzas", "Analista de Tesorería", 1200.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(10, "Jesús", "Flores", "110", "0414-1010101", "123", "Finanzas", "Especialista Impuestos", 1400.0, "Bloque Mixto", "Vacaciones", "Pagado", "Procesando"),
                Employee(11, "Karla", "Morales", "111", "0424-1121122", "123", "Finanzas", "Auditor de Nómina", 1300.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(12, "Luis", "Martínez", "112", "0412-1311313", "123", "Finanzas", "Analista Costos", 1100.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(13, "Mónica", "Castro", "113", "0416-1411414", "123", "Finanzas", "Gerente Finanzas", 2300.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                
                Employee(14, "Néstor", "Ríos", "114", "0414-1511515", "123", "Recursos Humanos", "Gerente RRHH", 1900.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(15, "Olga", "Suárez", "115", "0424-1611616", "123", "Recursos Humanos", "Reclutador Tech", 1000.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(16, "Pedro", "Ortiz", "116", "0412-1711717", "123", "Recursos Humanos", "Especialista Relaciones", 1150.0, "Bloque Mixto", "Próximas", "Pagado", "Procesando"),
                Employee(17, "Quintín", "Díaz", "117", "0416-1811818", "123", "Recursos Humanos", "Analista de Clima", 950.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(18, "Rosa", "Silva", "118", "0414-1911919", "123", "Recursos Humanos", "Médico Ocupacional", 1400.0, "Rotación Mañana", "Activo", "Pagado", "Procesando"),
                
                Employee(19, "Samuel", "Méndez", "119", "0424-2022020", "123", "Operaciones", "Director de Ruta", 1700.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(20, "Tatiana", "Rojas", "120", "0412-2122121", "123", "Operaciones", "Supervisor Despacho", 1200.0, "Rotación Mañana", "Activo", "Pagado", "Procesando"),
                Employee(21, "Urbano", "Vargas", "121", "0416-2232233", "123", "Operaciones", "Operador Logístico", 850.0, "Rotación Mañana", "Vacaciones", "Pagado", "Procesando"),
                Employee(22, "Valeria", "Alvarado", "122", "0414-2342343", "123", "Operaciones", "Planificador de Carga", 1100.0, "Rotación Tarde", "Activo", "Pagado", "Procesando"),
                Employee(23, "Walter", "Padrón", "123", "0424-2452454", "123", "Operaciones", "Coordinador Flota", 1300.0, "Rotación Noche", "Activo", "Pagado", "Procesando"),
                Employee(24, "Xavier", "Marín", "124", "0412-2562565", "123", "Operaciones", "Despachador Senior", 900.0, "Rotación Tarde", "Activo", "Pagado", "Procesando"),
                Employee(25, "Yolanda", "Brito", "125", "0416-2672676", "123", "Operaciones", "Control de Inventario", 1000.0, "Rotación Mañana", "Activo", "Pagado", "Procesando"),
                
                Employee(26, "Zuly", "Chacón", "126", "0414-2782787", "123", "Ventas", "Gerente Comercial", 2100.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(27, "Armando", "Lovera", "127", "0424-2892898", "123", "Ventas", "Key Account Manager", 1400.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(28, "Beatriz", "Meza", "128", "0412-2902909", "123", "Ventas", "Growth Hacker", 1300.0, "Bloque Mixto", "Próximas", "Pagado", "Procesando"),
                Employee(29, "César", "Palacios", "129", "0416-3013010", "123", "Ventas", "Ejecutivo Cuentas", 950.0, "Bloque Mixto", "Activo", "Pagado", "Procesando"),
                Employee(30, "Daniela", "Guzmán", "130", "0414-3123121", "123", "Ventas", "Content Creator", 1050.0, "Bloque Mixto", "Activo", "Pagado", "Procesando")
            )
            employeeDao.insertEmployees(seedList)
            
            // Seed base configurations
            employeeDao.insertConfig(SystemConfig("tasa_bcv", "39.45"))

            // Seed initial novelties
            employeeDao.insertNovelty(Novelty(0, "101", "Despliegue de Core exitoso.", System.currentTimeMillis() - 86400000))
            employeeDao.insertNovelty(Novelty(0, "101", "Mantenimiento preventivo el sábado.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "102", "Migración AWS completada.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "104", "Auditoría perimetral OK.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "107", "Backup mensual ejecutado.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "108", "Cierre fiscal de mes listo.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "111", "Procesamiento quincenal en marcha.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "114", "Evaluaciones de desempeño este mes.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "118", "Campañas de vacunación activas.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "119", "Optimización de despacho regional.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "123", "Revisión mecánica completada.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "126", "Nuevas metas trimestrales.", System.currentTimeMillis()))
            employeeDao.insertNovelty(Novelty(0, "128", "Campaña Web de captación activa.", System.currentTimeMillis()))
        }
    }
}
