package com.example.car_rental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.car_rental.R;
import com.example.car_rental.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvBrand;
        public TextView tvPickupDate;
        public TextView tvDropoffDate;
        public TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvPickupDate = itemView.findViewById(R.id.tvPickup);
            tvDropoffDate = itemView.findViewById(R.id.tvDropoff);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnLongClickListener(this);  //register long click action to this viewholder instance
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    } // close ViewHolder class

    //////////////////////////////////////////////////////////////////////
    // adapter class definitions

    private List<Booking> bookingListData;   // list of borrow objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public BookingAdapter(Context context, List<Booking> listData) {
        bookingListData = listData;
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
        View view = inflater.inflate(R.layout.booking_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Booking b = bookingListData.get(position);
        holder.tvBrand.setText(b.getCar().getBrand() + " " + b.getCar().getModel());
        holder.tvPickupDate.setText("Pick-Up Date: " + b.getPickup_date());
        holder.tvDropoffDate.setText("Drop-Off Date: " + b.getDropoff_date());
        holder.tvStatus.setText("Status: " + b.getStatus_booking());
    }

    @Override
    public int getItemCount() {
        return bookingListData.size();
    }

    /**
     * return borrow object for currently selected borrow record (index already set by long press in viewholder)
     * @return
     */
    public Booking getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && bookingListData !=null && currentPos<bookingListData.size()) {
            return bookingListData.get(currentPos);
        }
        return null;
    }
}
