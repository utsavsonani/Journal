package UI;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.R;
import com.example.journal.model.Journal;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class JournalRecyclerAdapter  extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder>  {

    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder holder, int position) {
            Journal journal = journalList.get(position);
            String imageUrl;

            holder.title.setText(journal.getTitle());
            holder.thoughts.setText(journal.getThought());
            holder.name.setText(journal.getUserName());

            String timeago = DateUtils.getRelativeTimeSpanString(journal.getTimeAdd().getSeconds() * 1000).toString();

            holder.dataAdd.setText(timeago);

            imageUrl = journal.getImageUrl();
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .fit()
                    .into(holder.image);
//            Picasso.get().invalidate(imageUrl);

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                title,
                thoughts,
                dataAdd,
                name;
        public ImageView image;
        String userId;
        String userName;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);

            context = ctx;
            title = itemView.findViewById(R.id.title_text);
            thoughts = itemView.findViewById(R.id.thoughts_text);
            dataAdd = itemView.findViewById(R.id.journal_time_list);
            image = itemView.findViewById(R.id.journal_image_list);
            name = itemView.findViewById(R.id.journal_row_userName);

        }
    }
}
