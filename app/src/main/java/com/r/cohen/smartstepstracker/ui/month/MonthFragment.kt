package com.r.cohen.smartstepstracker.ui.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.r.cohen.smartstepstracker.databinding.FragmentMonthBinding
import com.r.cohen.smartstepstracker.repo.DateTools

class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private val viewModel: MonthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with (binding.monthChart) {
            description.isEnabled = false
            legend.isEnabled = false

            xAxis.isEnabled = false
            val now = System.currentTimeMillis()
            xAxis.axisMinimum = DateTools.get30DaysAgo(now).toFloat()
            xAxis.axisMaximum = now.toFloat()

            axisRight.isEnabled = false
            setTouchEnabled(false)
            setDrawBorders(false)
            setNoDataText(getString(com.r.cohen.smartstepstracker.R.string.no_data_available))
            //setNoDataTextColor(ContextCompat.getColor(context, R.color.colorSecondaryVariant))
            axisLeft.typeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), com.r.cohen.smartstepstracker.R.font.quicksand_regular)
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

            with (binding.monthChart) {
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