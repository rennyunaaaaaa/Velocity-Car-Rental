package com.example.car_rental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.car_rental.R;
import com.example.car_rental.model.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public TextView tvBrand;
        public TextView tvModel;
        public TextView tvStatus;


        public ViewHolder(View itemView) {
            super(itemView);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnLongClickListener(this); //register long click action to this viewholder instance
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }

    } // close ViewHolder class

    //////////////////////////////////////////////////////////////////////
    // adapter class definitions

    private List<Car> carListData;   // list of book objects
    private Context mContext;       // activity context

    private int currentPos; // currently selected item (long press)


    public CarAdapter(Context context, List<Car> listData) {
        carListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.car_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Car m = carListData.get(position);
        holder.tvBrand.setText(m.getBrand());
        holder.tvModel.setText(m.getModel());
        holder.tvStatus.setText(m.getStatus());
    }

    @Override
    public int getItemCount() {
        return carListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in
     viewholder)
     * @return
     */
    public Car getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && carListData !=null && currentPos<carListData.size()) {
            return carListData.get(currentPos);
        }
        return null;
    }

}