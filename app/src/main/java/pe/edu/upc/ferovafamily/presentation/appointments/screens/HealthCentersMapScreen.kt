package pe.edu.upc.ferovafamily.presentation.appointments.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import pe.edu.upc.ferovafamily.domain.model.appointments.HealthCenter
import pe.edu.upc.ferovafamily.presentation.appointments.utils.RouteHelper

private val Crimson = Color(0xFF8B1A1A)
private val SoftPink = Color(0xFFF9E8E8)
private val SuccessGreen = Color(0xFF4CAF50)

// Función para crear un Bitmap desde un Drawable
private fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    return bitmap
}

// Crear un marcador simple con un círculo de color
private fun createSimpleMarkerBitmap(context: Context, color: Color, sizeDp: Int = 48): BitmapDrawable {
    val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        this.color = color.toArgb()
        isAntiAlias = true
    }
    val radius = sizePx / 2f
    canvas.drawCircle(radius, radius, radius, paint)
    return BitmapDrawable(context.resources, bitmap)
}

// Crear marcador desde drawable
private fun createMarkerFromDrawable(
    context: Context,
    drawableId: Int,
    tintColor: Color,
    sizeDp: Int = 48
): BitmapDrawable? {
    return try {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        if (drawable == null) return null

        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        val mutableDrawable = drawable.mutate()
        mutableDrawable.setTint(tintColor.toArgb())

        val bitmap = drawableToBitmap(mutableDrawable, sizePx, sizePx)
        BitmapDrawable(context.resources, bitmap)
    } catch (e: Exception) {
        null
    }
}

// ── Mapa OSMDroid ────────────────────────────────────────────────

@Composable
private fun OSMMapView(
    centers: List<HealthCenter>,
    userLocation: Pair<Double, Double>,
    selectedCenterId: String?,
    onMapReady: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val routeHelper = remember { RouteHelper() }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Función para dibujar la ruta (llamada desde LaunchedEffect)
    fun drawRouteToSelectedCenter() {
        if (selectedCenterId == null) return

        val mapView = mapViewRef ?: return
        val selectedCenter = centers.find { it.id == selectedCenterId } ?: return

        coroutineScope.launch {
            val start = GeoPoint(userLocation.first, userLocation.second)
            val end = GeoPoint(selectedCenter.location.latitude, selectedCenter.location.longitude)
            val route = routeHelper.getRoute(start, end)
            if (route != null) {
                routeHelper.clearRoutes(mapView)
                routeHelper.drawRoute(mapView, route.points)
                // Ajustar el zoom para mostrar toda la ruta
                val boundingBox = org.osmdroid.util.BoundingBox(
                    maxOf(start.latitude, end.latitude),
                    maxOf(start.longitude, end.longitude),
                    minOf(start.latitude, end.latitude),
                    minOf(start.longitude, end.longitude)
                )
                mapView.zoomToBoundingBox(boundingBox, true)
            }
        }
    }

    // Cuando cambia la posta seleccionada, dibujar la ruta
    LaunchedEffect(selectedCenterId) {
        drawRouteToSelectedCenter()
    }

    AndroidView(
        factory = { context ->
            Configuration.getInstance().apply {
                load(
                    context,
                    context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                )
                userAgentValue = context.packageName
            }

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                isTilesScaledToDpi = true
                controller.setZoom(15.0)
                mapViewRef = this
                onMapReady(this)
            }
        },
        update = { view ->
            // Limpiar overlays (pero mantener la ruta que ya se dibujó)
            val markersToKeep = mutableListOf<Any>()

            // Guardar la ruta actual si existe
            val existingRoutes = routeHelper.getRoutes(view)

            view.overlays.clear()

            // Restaurar la ruta si existe
            existingRoutes.forEach { view.overlays.add(it) }

            // Centrar en ubicación del usuario (solo la primera vez)
            val centerPoint = GeoPoint(userLocation.first, userLocation.second)
            view.controller.setCenter(centerPoint)

            // ── Marker del usuario ──
            try {
                var userIcon = createMarkerFromDrawable(ctx, R.drawable.home_pin, Color(0xFF2196F3), 56)
                if (userIcon == null) {
                    userIcon = createSimpleMarkerBitmap(ctx, Color(0xFF2196F3), 56)
                }

                Marker(view).apply {
                    position = centerPoint
                    title = "Tu ubicación"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = userIcon
                    view.overlays.add(this)
                }
            } catch (e: Exception) {
                Marker(view).apply {
                    position = centerPoint
                    title = "Tu ubicación"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    view.overlays.add(this)
                }
            }

            // ── Markers de las postas ──
            centers.forEach { healthCenter ->
                try {
                    var centerIcon = createMarkerFromDrawable(ctx, R.drawable.map_pin_heart, Crimson, 70)
                    if (centerIcon == null) {
                        centerIcon = createSimpleMarkerBitmap(ctx, Crimson, 70)
                    }

                    Marker(view).apply {
                        position = GeoPoint(
                            healthCenter.location.latitude,
                            healthCenter.location.longitude
                        )
                        title = healthCenter.name
                        snippet = String.format("%.1f km · %s", healthCenter.distanceKm, healthCenter.address.take(50))
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = centerIcon
                        view.overlays.add(this)
                    }
                } catch (e: Exception) {
                    Marker(view).apply {
                        position = GeoPoint(
                            healthCenter.location.latitude,
                            healthCenter.location.longitude
                        )
                        title = healthCenter.name
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        view.overlays.add(this)
                    }
                }
            }

            view.invalidate()
        },
        modifier = modifier
    )
}

// Necesitamos agregar esta función a RouteHelper
private fun RouteHelper.getRoutes(mapView: MapView): List<Polyline> {
    return mapView.overlays.filterIsInstance<Polyline>()
}

// ── Pantalla principal ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCentersMapScreen(
    onCenterClick: (centerId: String) -> Unit,
    viewModel: AppointmentsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var selectedCenterId by remember { mutableStateOf<String?>(null) }
    var showRouteInfo by remember { mutableStateOf(false) }
    var routeDistance by remember { mutableStateOf(0.0) }
    var routeDuration by remember { mutableStateOf(0) }
    val context = LocalContext.current


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (viewModel.hasLocationPermission()) {
            viewModel.onPermissionResult(true)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun centerMapOnUser() {
        mapView?.let { view ->
            val centerPoint = GeoPoint(state.userLocation.first, state.userLocation.second)
            view.controller.animateTo(centerPoint)
            view.controller.setZoom(15.0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Mapa ──
        OSMMapView(
            centers = state.healthCenters,
            userLocation = state.userLocation,
            selectedCenterId = selectedCenterId,
            onMapReady = { view -> mapView = view },
            modifier = Modifier.fillMaxSize()
        )

        // Botón para centrar en mi ubicación
        FloatingActionButton(
            onClick = {
                viewModel.loadNearbyFacilities()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            containerColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Recargar postas",
                tint = Crimson
            )
        }

        // Panel de información de ruta
        if (showRouteInfo && selectedCenterId != null) {
            val selectedCenter = state.healthCenters.find { it.id == selectedCenterId }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ruta a ${selectedCenter?.name ?: "posta"}",
                            fontWeight = FontWeight.Bold,
                            color = Crimson
                        )
                        IconButton(
                            onClick = {
                                showRouteInfo = false
                                selectedCenterId = null
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Distancia: ${String.format("%.1f", routeDistance)} km",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Duración aproximada: $routeDuration minutos",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val center = selectedCenter
                            if (center != null) {
                                val uri = "http://maps.google.com/maps?daddr=${center.location.latitude},${center.location.longitude}"
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
                                context.startActivity(intent)  // ← Usa context aquí
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir en Google Maps", color = Color.White)
                    }
                }
            }
        }

        // Pantalla de carga inicial
        if ((state.isLoadingLocation || state.isLoadingCenters) && state.healthCenters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Crimson)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Buscando postas cercanas...",
                        color = Crimson,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // ── TopBar flotante ──
        TopAppBar(
            title = {
                Column {
                    Text(
                        "Postas cercanas",
                        fontWeight = FontWeight.Bold,
                        color = Crimson
                    )
                    if (!state.isLoadingCenters && state.healthCenters.isNotEmpty()) {
                        Text(
                            text = "${state.healthCenters.size} centros encontrados",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White.copy(alpha = 0.93f),
                titleContentColor = Crimson
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // ── Lista flotante inferior ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = 320.dp)
                .background(
                    Color.White.copy(alpha = 0.97f),
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoadingCenters -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Crimson,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.5f.dp
                        )
                    }
                }

                state.healthCenters.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay postas cercanas a tu ubicación",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.healthCenters, key = { it.id }) { center ->
                            HealthCenterListItem(
                                center = center,
                                onSeeDetails = { onCenterClick(center.id) },
                                onGetDirections = {
                                    selectedCenterId = center.id
                                    routeDistance = center.distanceKm
                                    routeDuration = (center.distanceKm * 2).toInt()
                                    showRouteInfo = true
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

// ── Card de cada posta ────────────────────────────────────────────
@Composable
private fun HealthCenterListItem(
    center: HealthCenter,
    onSeeDetails: () -> Unit,
    onGetDirections: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SoftPink, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = Crimson,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = center.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = center.address.take(40),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (center.isActive) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SuccessGreen, RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Activo",
                                style = MaterialTheme.typography.bodySmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%.1f km", center.distanceKm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Crimson,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSeeDetails,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ver detalles",
                        color = Crimson,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = onGetDirections,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cómo llegar",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}