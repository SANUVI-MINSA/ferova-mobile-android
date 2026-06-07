package pe.edu.upc.ferovafamily.presentation.appointments.screens

import android.Manifest
import android.content.Context
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import pe.edu.upc.ferovafamily.presentation.appointments.model.HealthCenter

private val Crimson = Color(0xFF8B1A1A)
private val SoftPink = Color(0xFFF9E8E8)
private val SuccessGreen = Color(0xFF4CAF50)

//Funcion para mostrar correctamente los iconos en el mapa

fun Context.vectorToBitmap(drawableId: Int, tintColor: Int, sizeDp: Int = 36): BitmapDrawable {
    val sizePx = (sizeDp * resources.displayMetrics.density).toInt()
    val drawable = ContextCompat.getDrawable(this, drawableId)!!.mutate()
    drawable.setTint(tintColor)

    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, sizePx, sizePx)
    drawable.draw(canvas)

    return bitmap.toDrawable(resources)
}

// ── Mapa OSMDroid (OpenStreetMap, sin API key) ────────────────────────────────

@Composable
private fun OSMMapView(
    centers: List<HealthCenter>,
    userCenter: Pair<Double, Double>,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    AndroidView(
        factory = { context ->
            Configuration.getInstance().apply {
                load(
                    context,
                    context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
                )
                userAgentValue = context.packageName
            }

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                isTilesScaledToDpi = true
            }
        },
        update = { view ->
            val centerPoint = GeoPoint(userCenter.first, userCenter.second)
            view.controller.setZoom(14.0)
            view.controller.setCenter(centerPoint)

            // Limpiar overlays para no duplicarlos en cada actualización
            view.overlays.clear()

            // Marker del usuario
            view.overlays.add(Marker(view).apply {
                position = centerPoint
                title = "Tu ubicación"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ctx.vectorToBitmap(R.drawable.home_pin, "#1976D2".toColorInt())
            })

            // Markers de postas
            centers.forEach { healthCenter ->
                view.overlays.add(Marker(view).apply {
                    position = GeoPoint(
                        healthCenter.location.latitude,
                        healthCenter.location.longitude
                    )
                    title = healthCenter.name
                    snippet = "${healthCenter.distanceKm} km · ${healthCenter.address}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ctx.vectorToBitmap(R.drawable.map_pin_heart, "#8B1A1A".toColorInt())
                })
            }
            view.invalidate()
        },
        modifier = modifier
    )
}

// ── Pantalla principal ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCentersMapScreen(
    onCenterClick: (centerId: String) -> Unit,
    viewModel: AppointmentsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
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

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Mapa siempre presente para evitar reinicializaciones costosas ──
        OSMMapView(
            centers = state.healthCenters,
            userCenter = state.userLocation,
            modifier = Modifier.fillMaxSize()
        )

        // Pantalla de carga inicial (solo si no tenemos datos aún)
        if ((state.isLoadingLocation || state.isLoadingCenters) && state.healthCenters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Crimson)
            }
        }

        // ── TopBar flotante ───────────────────────────
        TopAppBar(
            title = {
                Text(
                    "Postas encontradas\n ${state.userLocation.first} - ${state.userLocation.second}\n${state.healthCenters.size}",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White.copy(alpha = 0.93f),
                titleContentColor = Crimson
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // ── Lista flotante inferior ───────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = 300.dp)
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

            Spacer(Modifier.height(8.dp))

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
                            strokeWidth = 2.5.dp
                        )
                    }
                }

                state.healthCenters.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay postas cercanas a su ubicación",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.healthCenters, key = { it.id }) { center ->
                            HealthCenterListItem(
                                center = center,
                                onSeeDetails = { onCenterClick(center.id) }
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }

                }
            }
        }
    }
}

// ── Card de cada posta ────────────────────────────────────────────────────────
@Composable
private fun HealthCenterListItem(
    center: HealthCenter,
    onSeeDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(SoftPink, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Crimson)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = center.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(4.dp))
                if (center.isActive) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    SuccessGreen,
                                    androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Activo",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onSeeDetails,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver detalles", color = Color.White)
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${center.distanceKm} km",
                style = MaterialTheme.typography.bodySmall,
                color = Crimson,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
