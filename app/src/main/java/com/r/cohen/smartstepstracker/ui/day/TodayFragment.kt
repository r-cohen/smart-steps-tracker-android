package com.r.cohen.smartstepstracker.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.r.cohen.smartstepstracker.R
import com.r.cohen.smartstepstracker.databinding.FragmentTodayBinding
import java.util.*

class TodayFragment : Fragment() {
    private lateinit var binding: FragmentTodayBinding
    private val viewModel: TodayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodayBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with (binding.dayChart) {
            description.isEnabled = false
            legend.isEnabled = false

            xAxis.isEnabled = false
            val now = System.currentTimeMillis()
            xAxis.axisMinimum = now.toFloat()
            val cal = Calendar.getInstance()
            cal.timeInMillis = now
            cal.add(Calendar.DATE, 1)
            xAxis.axisMaximum = cal.timeInMillis.toFloat()

            axisRight.isEnabled = false
            setTouchEnabled(false)
            setDrawBorders(false)
            setNoDataText(getString(R.string.no_data_available))
            //setNoDataTextColor(ContextCompat.getColor(context, R.color.colorSecondaryVariant))
            axisLeft.typeface = ResourcesCompat.getFont(requireContext(), R.font.quicksand_regular)
        }

        viewModel.measurementsDataSet.observe(viewLifecycleOwner) { dataset ->
            with (dataset) {
                mode = LineDataSet.Mode.LINEAR
                setDrawCircles(false)
                setDrawFilled(true)
                requireContext().let { ctx ->
                    //color = ContextCompat.getColor(ctx, R.color.pr)
                    //fillDrawable = ContextCompat.getDrawable(ctx, R.drawable.chart_fill_gradient)
                }
            }

            val lineData = LineData(dataset)
            lineData.setDrawValues(false)

            with (binding.dayChart) {
                data = lineData
                notifyDataSetChanged()
                invalidate()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribeToEvents()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unsubscribeEvents()
    }
}