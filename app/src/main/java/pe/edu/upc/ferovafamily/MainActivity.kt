package pe.edu.upc.ferovafamily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pe.edu.upc.ferovafamily.presentation.navigation.NavGraph
import pe.edu.upc.ferovafamily.presentation.theme.FerovaFamilyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FerovaFamilyTheme {
                NavGraph()
            }
        }
    }
}