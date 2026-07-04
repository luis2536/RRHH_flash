package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Employee
import com.example.data.Novelty
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val tasaBCV by viewModel.tasaBCV.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.toastMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (currentUser != null || isAdminMode) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "CORE ",
                                color = TextLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "NEXUS",
                                color = TechBlue,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Logo Core Nexus",
                            tint = TechBlue,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(24.dp)
                        )
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                if (currentUser != null) {
                                    Text(
                                        text = "${currentUser!!.name} ${currentUser!!.lastname}",
                                        color = TextLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = currentUser!!.role,
                                        color = TechBlue,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 9.sp
                                    )
                                } else if (isAdminMode) {
                                    Text(
                                        text = "Consola TI Maestro",
                                        color = TextLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "SuperUser",
                                        color = DangerRed,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x1AFF4757),
                                    contentColor = DangerRed
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .testTag("logout_button")
                                    .border(1.dp, DangerRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .height(28.dp)
                            ) {
                                Text("Cerrar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BgBase.copy(alpha = 0.95f)
                    ),
                    modifier = Modifier.border(0.dp, Color.White.copy(alpha = 0.05f))
                )
            }
        },
        containerColor = BgBase
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                currentUser == null && !isAdminMode -> {
                    AuthView(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                currentUser != null -> {
                    WorkerView(
                        viewModel = viewModel,
                        employee = currentUser!!,
                        tasaBCV = tasaBCV,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                isAdminMode -> {
                    AdminView(
                        viewModel = viewModel,
                        tasaBCV = tasaBCV,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun AuthView(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var cid by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val error by viewModel.loginError.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .background(BgBase)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        ) {
            // Settings Logo Icon with Ambient Glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .background(SurfaceSolid, RoundedCornerShape(36.dp))
                    .border(1.dp, TechBlue.copy(alpha = 0.3f), RoundedCornerShape(36.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = TechBlue,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Branding Titles
            Row {
                Text(
                    text = "CORE ",
                    color = TextLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "NEXUS",
                    color = TechBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Text(
                text = "Enterprise Resource Management",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Card with glass effect border
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSolid, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Llave de Acceso de Personal",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Error State Box
                AnimatedVisibility(
                    visible = error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    error?.let {
                        Text(
                            text = "⚠ $it",
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .background(DangerRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                    }
                }

                // CID Input Field
                OutlinedTextField(
                    value = cid,
                    onValueChange = { cid = it },
                    label = { Text("Cédula de Identidad", color = TextMuted) },
                    placeholder = { Text("Ej: 101, 102...", color = TextMuted.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = TechBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = BgBase,
                        unfocusedContainerColor = BgBase
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_cid_input")
                        .padding(bottom = 12.dp)
                )

                // Password Input Field
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Código de Seguridad", color = TextMuted) },
                    placeholder = { Text("••••••••", color = TextMuted.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = TechBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = BgBase,
                        unfocusedContainerColor = BgBase
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_pass_input")
                        .padding(bottom = 16.dp)
                )

                // Action buttons
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.login(cid, pass)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechBlue,
                        contentColor = BgBase
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_btn")
                        .height(48.dp)
                ) {
                    Text(
                        text = "VALIDAR CREDENCIALES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.quickAdmin()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = TextLight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_admin_btn")
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .height(48.dp)
                ) {
                    Text(
                        text = "ACCESO GERENCIAL TI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Estatus Infraestructura Web: ",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "ONLINE",
                    color = FinanceGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WorkerView(
    viewModel: MainViewModel,
    employee: Employee,
    tasaBCV: Double,
    modifier: Modifier = Modifier
) {
    val novelties by viewModel.currentNovelties.collectAsState()

    LazyColumn(
        modifier = modifier
            .background(BgBase)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Welcome Header
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Panel de Control ",
                        color = TextLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Especialista",
                        color = TechBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Monitoreo de nómina, jornadas y reportes internos.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // 💰 Previsualización Analítica de Haberes Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSolid, RoundedCornerShape(16.dp))
                    .border(1.dp, FinanceGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰 Previsualización Analítica de Haberes",
                        color = FinanceGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    // Vacation status badge
                    Box(
                        modifier = Modifier
                            .background(
                                when (employee.vacationStatus) {
                                    "Activo" -> FinanceGreen.copy(alpha = 0.1f)
                                    "Vacaciones" -> DangerRed.copy(alpha = 0.1f)
                                    else -> WarningOrange.copy(alpha = 0.1f)
                                },
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (employee.vacationStatus) {
                                "Activo" -> "Operando en Planta"
                                "Vacaciones" -> "Periodo Vacacional"
                                else -> "Periodo Próximo Asignado"
                            },
                            color = when (employee.vacationStatus) {
                                "Activo" -> FinanceGreen
                                "Vacaciones" -> DangerRed
                                else -> WarningOrange
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nominal Assignment USD & VES
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(BgBase, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "ASIGNACIÓN NOMINAL MENSUAL",
                            color = TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%,.2f", employee.salaryUSD)}",
                            color = TextLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Bs. ${String.format(Locale.US, "%,.2f", employee.salaryUSD * tasaBCV)}",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Payroll settlement badge status
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(BgBase, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ESTATUS DE LIQUIDACIÓN",
                            color = TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val isFullyPaid = employee.q1 == "Pagado" && employee.q2 == "Pagado"
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isFullyPaid) FinanceGreen.copy(alpha = 0.1f) else WarningOrange.copy(alpha = 0.1f),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isFullyPaid) "Mes Liquidado ✓" else "1era Quincena Cobrada",
                                color = if (isFullyPaid) FinanceGreen else WarningOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fortnight break down
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgBase.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Estatus de Ciclo Quincenal:",
                        color = TextLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("1era Quincena (Día 15): ", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = employee.q1.uppercase(),
                                color = if (employee.q1 == "Pagado") FinanceGreen else WarningOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("2da Quincena (Día 30): ", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = if (employee.q2 == "Pagado") "PAGADO" else "PROCESANDO",
                                color = if (employee.q2 == "Pagado") FinanceGreen else WarningOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 📅 Rotación Operativa Semanal Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSolid, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "📅 Rotación Operativa Semanal",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Bloque Asignado: ", color = TextMuted, fontSize = 11.sp)
                    Text(text = employee.shift, color = TechBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Days of week grid
                val days = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    days.forEach { day ->
                        val isWorkingDay = when {
                            employee.shift.contains("Noche") -> day != "Dom"
                            employee.shift.contains("Mañana") || employee.shift.contains("Tarde") || employee.shift.contains("Mixto") -> day != "Sab" && day != "Dom"
                            else -> true
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isWorkingDay) TechBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.02f))
                                .border(
                                    1.dp,
                                    if (isWorkingDay) TechBlue.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day,
                                color = if (isWorkingDay) TechBlue else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isWorkingDay) "Activo" else "Libre",
                                color = if (isWorkingDay) TechBlue.copy(alpha = 0.8f) else TextMuted.copy(alpha = 0.5f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 🔔 Alertas y Novedades TI Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSolid, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔔 Alertas y Novedades TI / Operaciones",
                        color = TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (novelties.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin novedades registradas esta semana.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        novelties.forEach { n ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BgBase, RoundedCornerShape(8.dp))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = n.message,
                                    color = TextLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(n.timestamp)),
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminView(
    viewModel: MainViewModel,
    tasaBCV: Double,
    modifier: Modifier = Modifier
) {
    val employees by viewModel.filteredEmployees.collectAsState()
    val allEmps by viewModel.allEmployees.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedDeptFilter by viewModel.departmentFilter.collectAsState()

    var editingTasaText by remember { mutableStateOf(tasaBCV.toString()) }
    var showNoveltyDialogEmployee by remember { mutableStateOf<Employee?>(null) }
    var newNoveltyMessage by remember { mutableStateOf("") }

    // Forms states for employee ingestion
    var fName by remember { mutableStateOf("") }
    var fLastName by remember { mutableStateOf("") }
    var fCid by remember { mutableStateOf("") }
    var fPhone by remember { mutableStateOf("") }
    var fPass by remember { mutableStateOf("") }
    var fDept by remember { mutableStateOf("Tecnología") }
    var fRole by remember { mutableStateOf("") }
    var fSalary by remember { mutableStateOf("") }
    var fShift by remember { mutableStateOf("Bloque Mixto") }

    LaunchedEffect(tasaBCV) {
        editingTasaText = tasaBCV.toString()
    }

    // Dropdown list models
    val departments = listOf("Tecnología", "Finanzas", "Recursos Humanos", "Operaciones", "Ventas")
    val filterDepts = listOf("TODOS") + departments
    val shifts = listOf("Bloque Mixto", "Rotación Mañana", "Rotación Tarde", "Rotación Noche")
    val vacationStatuses = listOf("Activo", "Vacaciones", "Próximas")
    val qStatuses = listOf("Pendiente", "Pagado")
    val q2Statuses = listOf("Procesando", "Pagado")

    LazyColumn(
        modifier = modifier
            .background(BgBase)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Master Console Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Consola Central ",
                            color = TextLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Corporativa",
                            color = TechBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Infraestructura de Gestión de Datos, Finanzas y Cuentas Operativas.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Tasa BCV Dynamic input
                Box(
                    modifier = Modifier
                        .background(TechBlue.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                        .border(1.dp, TechBlue.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "TASA CAMBIARIA (BCV)",
                            color = TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("Bs. ", color = FinanceGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            BasicTextField(
                                value = editingTasaText,
                                onValueChange = {
                                    editingTasaText = it
                                    it.toDoubleOrNull()?.let { d -> viewModel.updateTasaBCV(d) }
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    color = TextLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .width(60.dp)
                                    .testTag("admin_tasa_input")
                            )
                        }
                    }
                }
            }
        }

        // Global Stats Grid
        item {
            val totalPayrollUSD = allEmps.sumOf { it.salaryUSD }
            val totalVacationsCount = allEmps.count { it.vacationStatus == "Vacaciones" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total workforce
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceSolid, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                        .border(
                            width = 3.dp,
                            brush = Brush.verticalGradient(listOf(TechBlue, TechBlue.copy(alpha = 0f))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text("FUERZA LABORAL", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${allEmps.size}",
                        color = TextLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text("Fichas en DB", color = TextMuted, fontSize = 9.sp)
                }

                // Global monthly nominal payroll
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .background(SurfaceSolid, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                        .border(
                            width = 3.dp,
                            brush = Brush.verticalGradient(listOf(FinanceGreen, FinanceGreen.copy(alpha = 0f))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text("NÓMINA MENSUAL", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", totalPayrollUSD)}",
                        color = TextLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Bs. ${String.format(Locale.US, "%,.0f", totalPayrollUSD * tasaBCV)}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Employees in vacation
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceSolid, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                        .border(
                            width = 3.dp,
                            brush = Brush.verticalGradient(listOf(WarningOrange, WarningOrange.copy(alpha = 0f))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text("VACACIONES", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$totalVacationsCount",
                        color = TextLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text("Reemplazos", color = TextMuted, fontSize = 9.sp)
                }
            }
        }

        // Advanced Search Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSolid, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "🔍 Motor de Búsqueda Avanzada de Personal",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Query Text input
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Buscar por Nombre, Rol o Cédula...", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = TechBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                            focusedContainerColor = BgBase,
                            unfocusedContainerColor = BgBase
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("admin_search_input")
                            .height(48.dp)
                    )

                    // Department Filter Spinner Dropdown
                    var showFilterSpinner by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgBase)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .clickable { showFilterSpinner = true }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedDeptFilter,
                                color = TextLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = TechBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showFilterSpinner,
                            onDismissRequest = { showFilterSpinner = false },
                            modifier = Modifier.background(SurfaceSolid)
                        ) {
                            filterDepts.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept, color = TextLight, fontSize = 11.sp) },
                                    onClick = {
                                        viewModel.setDepartmentFilter(dept)
                                        showFilterSpinner = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Database Table Matrix Title
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "🖥️ Repositorio y Matriz de Control de Base de Datos",
                    color = TechBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "La modificación de los campos impacta inmediatamente la vista del portal del trabajador.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }
        }

        // Database Records list
        if (employees.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron registros de personal.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            items(employees, key = { it.id }) { emp ->
                EmployeeAdminRow(
                    emp = emp,
                    tasaBCV = tasaBCV,
                    shifts = shifts,
                    vacationStatuses = vacationStatuses,
                    qStatuses = qStatuses,
                    q2Statuses = q2Statuses,
                    onShiftChange = { viewModel.updateEmployeeShift(emp.id, it) },
                    onVacationChange = { viewModel.updateEmployeeVacationStatus(emp.id, it) },
                    onQ1Change = { viewModel.updateEmployeeQ1(emp.id, it) },
                    onQ2Change = { viewModel.updateEmployeeQ2(emp.id, it) },
                    onAddNovelty = { showNoveltyDialogEmployee = emp }
                )
            }
        }

        // CRUD Intake Form
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSolid, RoundedCornerShape(16.dp))
                    .border(1.dp, HrPurple.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "➕ Formulario de Captación y Alta de Personal (CRUD)",
                    color = HrPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Input rows
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fName,
                            onValueChange = { fName = it },
                            placeholder = { Text("Nombres", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_name_input")
                        )
                        OutlinedTextField(
                            value = fLastName,
                            onValueChange = { fLastName = it },
                            placeholder = { Text("Apellidos", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_lastname_input")
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fCid,
                            onValueChange = { fCid = it },
                            placeholder = { Text("Cédula de Identidad", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_cid_input")
                        )
                        OutlinedTextField(
                            value = fPhone,
                            onValueChange = { fPhone = it },
                            placeholder = { Text("Teléfono de Contacto", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_phone_input")
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fPass,
                            onValueChange = { fPass = it },
                            placeholder = { Text("Clave Asignada", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_pass_input")
                        )

                        // Department Dropdown
                        var showFormDeptSpinner by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgBase)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .clickable { showFormDeptSpinner = true }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fDept,
                                    color = TextLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = HrPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showFormDeptSpinner,
                                onDismissRequest = { showFormDeptSpinner = false },
                                modifier = Modifier.background(SurfaceSolid)
                            ) {
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept, color = TextLight, fontSize = 11.sp) },
                                        onClick = {
                                            fDept = dept
                                            showFormDeptSpinner = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fRole,
                            onValueChange = { fRole = it },
                            placeholder = { Text("Cargo (Ej: Dev, Analista)", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_role_input")
                        )
                        OutlinedTextField(
                            value = fSalary,
                            onValueChange = { fSalary = it },
                            placeholder = { Text("Sueldo Base ($)", color = TextMuted.copy(alpha = 0.5f), fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                                focusedBorderColor = HrPurple, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("form_salary_input")
                        )
                    }

                    // Shift selection
                    var showFormShiftSpinner by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgBase)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .clickable { showFormShiftSpinner = true }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fShift,
                                color = TextLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = HrPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showFormShiftSpinner,
                            onDismissRequest = { showFormShiftSpinner = false },
                            modifier = Modifier.background(SurfaceSolid)
                        ) {
                            shifts.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, color = TextLight, fontSize = 11.sp) },
                                    onClick = {
                                        fShift = s
                                        showFormShiftSpinner = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val sal = fSalary.toDoubleOrNull() ?: 0.0
                        viewModel.createEmployee(
                            fName, fLastName, fCid, fPhone, fPass, fDept, fRole, sal, fShift
                        )
                        // Clear fields on success
                        if (fName.isNotBlank() && fLastName.isNotBlank() && fCid.isNotBlank() && sal > 0) {
                            fName = ""
                            fLastName = ""
                            fCid = ""
                            fPhone = ""
                            fPass = ""
                            fRole = ""
                            fSalary = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HrPurple,
                        contentColor = BgBase
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_form_button")
                        .height(48.dp)
                ) {
                    Text(
                        text = "INYECTAR FICHA EN BASE DE DATOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    // Modal dialog to add a novelty / IT alert
    if (showNoveltyDialogEmployee != null) {
        val emp = showNoveltyDialogEmployee!!
        Dialog(onDismissRequest = { showNoveltyDialogEmployee = null }) {
            Surface(
                color = SurfaceSolid,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechBlue.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Registrar Incidencia / Novedad",
                        color = TechBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Empleado: ${emp.name} ${emp.lastname} (Ficha #${emp.id})",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newNoveltyMessage,
                        onValueChange = { newNoveltyMessage = it },
                        placeholder = { Text("Describa la novedad o incidencia de TI/Operaciones...", color = TextMuted.copy(alpha = 0.4f), fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight, unfocusedTextColor = TextLight,
                            focusedBorderColor = TechBlue, unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                            focusedContainerColor = BgBase, unfocusedContainerColor = BgBase
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("novelty_message_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                showNoveltyDialogEmployee = null
                                newNoveltyMessage = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = TextMuted
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        ) {
                            Text("Cancelar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (newNoveltyMessage.isNotBlank()) {
                                    viewModel.addNovelty(emp.cid, newNoveltyMessage)
                                    showNoveltyDialogEmployee = null
                                    newNoveltyMessage = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TechBlue,
                                contentColor = BgBase
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_novelty_button")
                        ) {
                            Text("Guardar", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeAdminRow(
    emp: Employee,
    tasaBCV: Double,
    shifts: List<String>,
    vacationStatuses: List<String>,
    qStatuses: List<String>,
    q2Statuses: List<String>,
    onShiftChange: (String) -> Unit,
    onVacationChange: (String) -> Unit,
    onQ1Change: (String) -> Unit,
    onQ2Change: (String) -> Unit,
    onAddNovelty: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 8f
                shape = RoundedCornerShape(12.dp)
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceSolid, SurfaceSolid.copy(alpha = 0.9f))
                ),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        // First row: Name, Role, CID
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "${emp.name} ${emp.lastname}",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "${emp.dept} • ${emp.role} (#${emp.id})",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "Cédula: ${emp.cid}",
                    color = TechBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format(Locale.US, "%,.0f", emp.salaryUSD)}",
                    color = TextLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
                Text(
                    text = "Bs. ${String.format(Locale.US, "%,.0f", emp.salaryUSD * tasaBCV)}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.04f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // Actions: Dropdowns for custom configurations
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Shift Selector Spinner
            var showShiftSpinner by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BgBase)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                    .clickable { showShiftSpinner = true }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val label = if (emp.shift.contains("Mixto")) "Mixto" else if (emp.shift.contains("Mañana")) "Mañana" else if (emp.shift.contains("Tarde")) "Tarde" else "Noche"
                    Text(
                        text = label,
                        color = TextLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = TechBlue,
                        modifier = Modifier.size(12.dp)
                    )
                }
                DropdownMenu(
                    expanded = showShiftSpinner,
                    onDismissRequest = { showShiftSpinner = false },
                    modifier = Modifier.background(SurfaceSolid)
                ) {
                    shifts.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s, color = TextLight, fontSize = 10.sp) },
                            onClick = {
                                onShiftChange(s)
                                showShiftSpinner = false
                            }
                        )
                    }
                }
            }

            // Vacation Selector Spinner
            var showVacationSpinner by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when (emp.vacationStatus) {
                            "Activo" -> FinanceGreen.copy(alpha = 0.1f)
                            "Vacaciones" -> DangerRed.copy(alpha = 0.1f)
                            else -> WarningOrange.copy(alpha = 0.1f)
                        }
                    )
                    .border(
                        1.dp,
                        when (emp.vacationStatus) {
                            "Activo" -> FinanceGreen.copy(alpha = 0.3f)
                            "Vacaciones" -> DangerRed.copy(alpha = 0.3f)
                            else -> WarningOrange.copy(alpha = 0.3f)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { showVacationSpinner = true }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emp.vacationStatus,
                        color = when (emp.vacationStatus) {
                            "Activo" -> FinanceGreen
                            "Vacaciones" -> DangerRed
                            else -> WarningOrange
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = when (emp.vacationStatus) {
                            "Activo" -> FinanceGreen
                            "Vacaciones" -> DangerRed
                            else -> WarningOrange
                        },
                        modifier = Modifier.size(12.dp)
                    )
                }
                DropdownMenu(
                    expanded = showVacationSpinner,
                    onDismissRequest = { showVacationSpinner = false },
                    modifier = Modifier.background(SurfaceSolid)
                ) {
                    vacationStatuses.forEach { vs ->
                        DropdownMenuItem(
                            text = { Text(vs, color = TextLight, fontSize = 10.sp) },
                            onClick = {
                                onVacationChange(vs)
                                showVacationSpinner = false
                            }
                        )
                    }
                }
            }

            // Q1 status dropdown
            var showQ1Spinner by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (emp.q1 == "Pagado") FinanceGreen.copy(alpha = 0.08f) else WarningOrange.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        if (emp.q1 == "Pagado") FinanceGreen.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { showQ1Spinner = true }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Q1:${if (emp.q1 == "Pagado") "Pago" else "Pend"}",
                        color = if (emp.q1 == "Pagado") FinanceGreen else WarningOrange,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (emp.q1 == "Pagado") FinanceGreen else WarningOrange,
                        modifier = Modifier.size(12.dp)
                    )
                }
                DropdownMenu(
                    expanded = showQ1Spinner,
                    onDismissRequest = { showQ1Spinner = false },
                    modifier = Modifier.background(SurfaceSolid)
                ) {
                    qStatuses.forEach { qs ->
                        DropdownMenuItem(
                            text = { Text("Q1: $qs", color = TextLight, fontSize = 10.sp) },
                            onClick = {
                                onQ1Change(qs)
                                showQ1Spinner = false
                            }
                        )
                    }
                }
            }

            // Q2 status dropdown
            var showQ2Spinner by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (emp.q2 == "Pagado") FinanceGreen.copy(alpha = 0.08f) else WarningOrange.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        if (emp.q2 == "Pagado") FinanceGreen.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f),
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { showQ2Spinner = true }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Q2:${if (emp.q2 == "Pagado") "Pago" else "Proc"}",
                        color = if (emp.q2 == "Pagado") FinanceGreen else WarningOrange,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (emp.q2 == "Pagado") FinanceGreen else WarningOrange,
                        modifier = Modifier.size(12.dp)
                    )
                }
                DropdownMenu(
                    expanded = showQ2Spinner,
                    onDismissRequest = { showQ2Spinner = false },
                    modifier = Modifier.background(SurfaceSolid)
                ) {
                    q2Statuses.forEach { q2s ->
                        DropdownMenuItem(
                            text = { Text("Q2: $q2s", color = TextLight, fontSize = 10.sp) },
                            onClick = {
                                onQ2Change(q2s)
                                showQ2Spinner = false
                            }
                        )
                    }
                }
            }

            // Novelty / Alert dialog trigger
            Button(
                onClick = onAddNovelty,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x1A00D2FF),
                    contentColor = TechBlue
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                modifier = Modifier
                    .weight(0.8f)
                    .border(1.dp, TechBlue.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .height(28.dp)
            ) {
                Text("⚡ Nov", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
