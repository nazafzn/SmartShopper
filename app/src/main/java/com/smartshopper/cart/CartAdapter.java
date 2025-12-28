package com.smartshopper.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartshopper.R;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartUpdateListener onCartUpdateListener;

    public CartAdapter(List<CartItem> cartItems, OnCartUpdateListener onCartUpdateListener) {
        this.cartItems = cartItems;
        this.onCartUpdateListener = onCartUpdateListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProductIcon;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvQuantity;
        private TextView tvSubtotal;
        private ImageButton btnDecreaseQuantity;
        private ImageButton btnIncreaseQuantity;
        private ImageButton btnDeleteItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductIcon = itemView.findViewById(R.id.iv_cart_product_icon);
            tvProductName = itemView.findViewById(R.id.tv_cart_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_cart_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            btnDecreaseQuantity = itemView.findViewById(R.id.btn_decrease_quantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btn_increase_quantity);
            btnDeleteItem = itemView.findViewById(R.id.btn_delete_item);
        }

        public void bind(CartItem cartItem) {
            // TODO: Load image with a library like Glide or Picasso
            // ivProductIcon.setImageURI(cartItem.getImageUrl());

            tvProductName.setText(cartItem.getName());
            tvProductPrice.setText(String.format(Locale.getDefault(), "RM%.2f each", cartItem.getPrice()));
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            updateSubtotal(cartItem);

            btnIncreaseQuantity.setOnClickListener(v -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                updateSubtotal(cartItem);
                onCartUpdateListener.onCartUpdated();
            });

            btnDecreaseQuantity.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                    updateSubtotal(cartItem);
                    onCartUpdateListener.onCartUpdated();
                }
            });

            btnDeleteItem.setOnClickListener(v -> {
                cartItems.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                onCartUpdateListener.onCartUpdated();
            });
        }

        private void updateSubtotal(CartItem cartItem) {
            double subtotal = cartItem.getPrice() * cartItem.getQuantity();
            tvSubtotal.setText(String.format(Locale.getDefault(), "RM%.2f", subtotal));
        }
    }
}
