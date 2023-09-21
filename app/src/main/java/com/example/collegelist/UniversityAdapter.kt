import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegelist.R
import com.example.collegelist.University

class UniversityAdapter(private var universities: MutableList<University>) :
    RecyclerView.Adapter<UniversityAdapter.ViewHolder>() {
    private var isExpandedList = MutableList(universities.size) { false }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.university_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val university = universities[position]
        holder.universityNameTextView.text = university.name

        if (isExpandedList[position]) {
            holder.websiteTextView.visibility = View.VISIBLE
            holder.countryTextView.visibility = View.VISIBLE
        } else {
            holder.websiteTextView.visibility = View.GONE
            holder.countryTextView.visibility = View.GONE
        }
        // Bind other data as needed
    }

    override fun getItemCount(): Int {
        return universities.size
    }

     fun updateUniversities(newUniversities: MutableList<University>) {
        universities = newUniversities
        notifyDataSetChanged() // Notify the adapter that the dataset has changed
    }

    fun addUniversities(newUniversities: MutableList<University>) {
        universities.addAll(newUniversities)
        notifyDataSetChanged() // Notify the adapter that the dataset has changed
    }
    fun list(): MutableList<University> {
        return universities
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val universityNameTextView: TextView = itemView.findViewById(R.id.universityNameTextView)
        // Add references to other UI elements here
        val websiteTextView: TextView = itemView.findViewById(R.id.websiteTextView)
        val countryTextView: TextView = itemView.findViewById(R.id.countryTextView)
        init {
            universityNameTextView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    isExpandedList[position] = !isExpandedList[position]
                    notifyItemChanged(position)
                }// Notify the adapter that data has changed
            }
        }
    }

}