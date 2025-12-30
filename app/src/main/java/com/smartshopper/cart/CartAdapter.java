package com.smartshopper.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartshopper.R;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final OnCartUpdateListener onCartUpdateListener;

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

        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvQuantity;
        private final TextView tvSubtotal;
        private final ImageButton btnDecreaseQuantity;
        private final ImageButton btnIncreaseQuantity;
        private final ImageButton btnDeleteItem;
        private final View cartItemContainer;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_cart_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_cart_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            btnDecreaseQuantity = itemView.findViewById(R.id.btn_decrease_quantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btn_increase_quantity);
            btnDeleteItem = itemView.findViewById(R.id.btn_delete_item);
            cartItemContainer = itemView.findViewById(R.id.cart_item_container);
        }

        public void bind(CartItem cartItem) {

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

            cartItemContainer.setOnClickListener(v -> {
                onCartUpdateListener.onSpeakItemInCart(String.format(
                        "%d, %s, total price: %s",
                        cartItem.getQuantity(),
                        cartItem.getName(),
                        speakProductPrice(cartItem.getPrice() * cartItem.getQuantity())
                ));
            });
        }

        private String speakProductPrice(double price) {
            // Split the price into whole numbers and decimals
            int ringgit = (int) price;
            int cents = (int) Math.round((price - ringgit) * 100);

            StringBuilder priceString = new StringBuilder();

            // Handle Ringgit part
            if (ringgit > 0) {
                priceString.append(ringgit).append(" Ringgit");
            }

            // Handle Cents part
            if (cents > 0) {
                if (ringgit > 0) {
                    priceString.append(" and ");
                }
                priceString.append(cents).append(" cents");
            }

            // Handle cases where price might be 0.00
            if (ringgit == 0 && cents == 0) {
                return "Zero Ringgit";
            }

            return priceString.toString();
        }

        private void updateSubtotal(CartItem cartItem) {
            double subtotal = cartItem.getPrice() * cartItem.getQuantity();
            tvSubtotal.setText(String.format(Locale.getDefault(), "RM%.2f", subtotal));
        }
    }
}
