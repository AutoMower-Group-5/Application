import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMapBinding
import com.example.myapplication.ui.map.MapView
import com.example.myapplication.ui.map.MapViewModel

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var mapViewModel: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.MapView)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapView.bindViewModel(mapViewModel)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.unbindViewModel(mapViewModel)
    }
}
