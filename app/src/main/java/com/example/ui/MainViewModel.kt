package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Employee
import com.example.data.EmployeeRepository
import com.example.data.Novelty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModel(private val repository: EmployeeRepository) : ViewModel() {

    init {
        // Seed database if it is empty
        viewModelScope.launch {
            repository.seedIfNeeded()
        }
    }

    private val _currentUser = MutableStateFlow<Employee?>(null)
    val currentUser: StateFlow<Employee?> = _currentUser.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    val allEmployees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasaBCV: StateFlow<Double> = repository.getTasaBCVFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 39.45)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _departmentFilter = MutableStateFlow("TODOS")
    val departmentFilter: StateFlow<String> = _departmentFilter.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Dynamically retrieve novelties for the logged-in user
    val currentNovelties: StateFlow<List<Novelty>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getNoveltiesForEmployee(user.cid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered list of employees for the admin portal
    val filteredEmployees: StateFlow<List<Employee>> = combine(
        allEmployees,
        _searchQuery,
        _departmentFilter
    ) { employees, query, dept ->
        employees.filter { emp ->
            val fullName = "${emp.name} ${emp.lastname}".lowercase()
            val matchesSearch = query.isEmpty() ||
                    fullName.contains(query.lowercase()) ||
                    emp.role.lowercase().contains(query.lowercase()) ||
                    emp.cid.contains(query)
            
            val matchesDept = dept == "TODOS" || emp.dept == dept
            matchesSearch && matchesDept
        }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login(cid: String, pass: String) {
        viewModelScope.launch {
            _loginError.value = null
            if (cid.isBlank() || pass.isBlank()) {
                _loginError.value = "Ingrese credenciales completas."
                return@launch
            }
            val emp = repository.getEmployeeByCid(cid)
            if (emp != null && emp.pass == pass) {
                _currentUser.value = emp
                _isAdminMode.value = false
                _toastMessage.emit("Sesión iniciada: ${emp.name} ${emp.lastname}")
            } else {
                _loginError.value = "Ficha no encontrada o clave errónea."
            }
        }
    }

    fun quickAdmin() {
        viewModelScope.launch {
            _currentUser.value = null
            _isAdminMode.value = true
            _loginError.value = null
            _toastMessage.emit("Acceso Gerencial TI otorgado.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            _isAdminMode.value = false
            _loginError.value = null
            _toastMessage.emit("Sesión destruida de forma segura.")
        }
    }

    fun updateTasaBCV(newTasa: Double) {
        viewModelScope.launch {
            if (newTasa > 0) {
                repository.updateTasaBCV(newTasa)
                _toastMessage.emit("Tasa ajustada a Bs. $newTasa")
            }
        }
    }

    fun updateEmployeeShift(employeeId: Int, newShift: String) {
        viewModelScope.launch {
            val emp = allEmployees.value.find { it.id == employeeId }
            if (emp != null) {
                val updated = emp.copy(shift = newShift)
                repository.updateEmployee(updated)
                _toastMessage.emit("Ficha #${employeeId} modificada (Horario).")
                // If the updated employee is the currently logged-in user, refresh their details
                if (_currentUser.value?.id == employeeId) {
                    _currentUser.value = updated
                }
            }
        }
    }

    fun updateEmployeeVacationStatus(employeeId: Int, newStatus: String) {
        viewModelScope.launch {
            val emp = allEmployees.value.find { it.id == employeeId }
            if (emp != null) {
                val updated = emp.copy(vacationStatus = newStatus)
                repository.updateEmployee(updated)
                _toastMessage.emit("Ficha #${employeeId} modificada (Vacaciones).")
                if (_currentUser.value?.id == employeeId) {
                    _currentUser.value = updated
                }
            }
        }
    }

    fun updateEmployeeQ1(employeeId: Int, newQ1: String) {
        viewModelScope.launch {
            val emp = allEmployees.value.find { it.id == employeeId }
            if (emp != null) {
                val updated = emp.copy(q1 = newQ1)
                repository.updateEmployee(updated)
                _toastMessage.emit("Ficha #${employeeId} modificada (Q1).")
                if (_currentUser.value?.id == employeeId) {
                    _currentUser.value = updated
                }
            }
        }
    }

    fun updateEmployeeQ2(employeeId: Int, newQ2: String) {
        viewModelScope.launch {
            val emp = allEmployees.value.find { it.id == employeeId }
            if (emp != null) {
                val updated = emp.copy(q2 = newQ2)
                repository.updateEmployee(updated)
                _toastMessage.emit("Ficha #${employeeId} modificada (Q2).")
                if (_currentUser.value?.id == employeeId) {
                    _currentUser.value = updated
                }
            }
        }
    }

    fun addNovelty(employeeCid: String, message: String) {
        viewModelScope.launch {
            if (message.isNotBlank()) {
                val novelty = Novelty(employeeCid = employeeCid, message = message)
                repository.insertNovelty(novelty)
                _toastMessage.emit("Incidencia registrada para Ficha.")
            }
        }
    }

    fun createEmployee(
        name: String,
        lastname: String,
        cid: String,
        phone: String,
        pass: String,
        dept: String,
        role: String,
        salaryUSD: Double,
        shift: String
    ) {
        viewModelScope.launch {
            if (name.isBlank() || lastname.isBlank() || cid.isBlank() || salaryUSD <= 0) {
                _toastMessage.emit("Complete los campos obligatorios.")
                return@launch
            }
            // Check if duplicate CID
            val existing = repository.getEmployeeByCid(cid)
            if (existing != null) {
                _toastMessage.emit("La cédula ya está registrada.")
                return@launch
            }

            val newEmp = Employee(
                name = name,
                lastname = lastname,
                cid = cid,
                phone = phone,
                pass = pass.ifBlank { "123" },
                dept = dept,
                role = role.ifBlank { "Operador" },
                salaryUSD = salaryUSD,
                shift = shift,
                vacationStatus = "Activo",
                q1 = "Pendiente",
                q2 = "Procesando"
            )
            repository.insertEmployee(newEmp)
            // Add initial novelty
            repository.insertNovelty(Novelty(employeeCid = cid, message = "Ingreso nuevo al sistema."))
            _toastMessage.emit("Nueva ficha inyectada.")
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDepartmentFilter(dept: String) {
        _departmentFilter.value = dept
    }
}

class MainViewModelFactory(private val repository: EmployeeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
