package pe.edu.upc.ferovafamily.presentation.appointments.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import pe.edu.upc.ferovafamily.presentation.appointments.model.HealthCenter

private val Crimson      = Color(0xFF8B1A1A)
private val Cream        = Color(0xFFFDF8F8)
private val SoftPink     = Color(0xFFF9E8E8)
private val SuccessGreen = Color(0xFF4CAF50)

// ── Mapa OSMDroid (OpenStreetMap, sin API key) ────────────────────────────────

@Composable
private fun OSMMapView(
    centers: List<HealthCenter>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            // Configurar OSMDroid
            Configuration.getInstance().apply {
                load(ctx, ctx.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
                userAgentValue = ctx.packageName
            }

            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                isTilesScaledToDpi = true

                // Centrar en San Juan de Lurigancho, Lima
                val centerPoint = GeoPoint(-12.0250, -76.9990)
                controller.setZoom(14.0)
                controller.setCenter(centerPoint)

                // Marcadores de postas
                if (centers.isNotEmpty()) {
                    centers.forEach { center ->
                        val marker = Marker(this).apply {
                            position = GeoPoint(
                                center.location.latitude,
                                center.location.longitude
                            )
                            title   = center.name
                            snippet = "${center.distanceKm} km · ${center.address}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        overlays.add(marker)
                    }
                } else {
                    // Marcador de referencia cuando no hay datos del API
                    val marker = Marker(this).apply {
                        position = centerPoint
                        title    = "San Juan de Lurigancho"
                        snippet  = "Lima, Perú"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    overlays.add(marker)
                }
            }
        },
        modifier = modifier
    )
}

// ── Pantalla principal ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCentersMapScreen(
    onBack: (() -> Unit)? = null,
    onCenterClick: (centerId: String) -> Unit,
    viewModel: AppointmentsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Mapa a pantalla completa ──────────────────
        OSMMapView(
            centers = state.healthCenters,
            modifier = Modifier.fillMaxSize()
        )

        // ── TopBar flotante ───────────────────────────
        TopAppBar(
            title = { Text("Postas Cercanas", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Crimson
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

        // ── Lista flotante inferior ───────────────────
        if (state.isLoadingCenters) {
            CircularProgressIndicator(
                color = Crimson,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.healthCenters.isNotEmpty()) {
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
                                .background(SuccessGreen, androidx.compose.foundation.shape.CircleShape)
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
