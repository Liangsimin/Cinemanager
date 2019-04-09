package net.lzzy.cinemanager.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.sip.SipSession;
import android.service.autofill.CustomDescription;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.utils.AppUtils;
import net.lzzy.simpledatepicker.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by lzzy_gxy on 2019/3/27.
 * Description:
 */
public class AddOrdersFragment extends BaseFragment {

    private OnFragmentIntInteractionListener searchListener;
    private CustomDatePicker picker;
    private TextView tvDate;
    private Spinner spCinema;
    private EditText edtName;
    private EditText edtPrice;
    private ImageView imgQRCode;
    private List<Cinema> cinemas;
    private Spinner spCinemas;
    private OnOrderCreatedListener listener;


    @Override
    protected void populate() {
        searchListener.hideSearch();
        initViews();
        initDatePicker();
        cinemas = CinemaFactory.getInstance().get();
        spCinema.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cinemas));
        addListeners();


    }

    private void addListeners() {
       find(R.id.add_order_movie_btn_cancel).setOnClickListener(v -> searchListener.cancelAddOrder());
       find(R.id.add_order_movie_btn_save).setOnClickListener(v -> {
           String name = edtName.getText().toString();
           String price = edtPrice.getText().toString();
           String location = spCinema.getSelectedItem().toString();
           String time = tvDate .getText().toString();
               if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                   Toast.makeText(getContext(), "信息需要完整", Toast.LENGTH_SHORT).show();
                   return;
               }
               String content = "[" + name +"]" + time + "\n" +location +"票价" +
                       price + "元";
           imgQRCode.setImageBitmap(AppUtils.createQRCodeBitmap(content,300,300));


       });
       imgQRCode.setOnLongClickListener(v -> {
           Bitmap bitmap = ((BitmapDrawable)imgQRCode.getDrawable()).getBitmap();
           Toast.makeText(getContext(),AppUtils.readQRCode(bitmap),
                   Toast.LENGTH_SHORT).show();
           return true;
       });find(R.id.add_order_movie_btn_save).setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String strPrice = edtPrice.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(strPrice)) {
                Toast.makeText(getContext(), "信息需要完整", Toast.LENGTH_SHORT).show();
                return;
            }
            float price;
            try {
                price = Float.parseFloat(strPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "数字格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
            Order order = new Order();
            Cinema cinema = cinemas.get(spCinemas.getSelectedItemPosition());
            order.setCinemaId(cinema.getId());
            order.setMovie(name);
            order.setMovieTime(tvDate.getText().toString());
            order.setPrice(price);
            listener.saveOrder(order);

        });

    }

    private void initViews() {
        spCinema = find(R.id.add_order_sp1_cinemas);
        tvDate = find(R.id.add_order_tv_time);
        edtName = find(R.id.add_order_movie_edt_name);
        edtPrice = find(R.id.add_order_movie_edt_price);
        imgQRCode = find(R.id.add_order_movie_imgQRCode);
        find(R.id.add_order_layout_time).setOnClickListener(v ->
                picker.show(tvDate.getText().toString()));
    }
    private void initDatePicker(){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            String now = sdf.format(new Date());
            tvDate.setText(now);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MARCH,1);
            String end=sdf.format(calendar.getTime());
            picker = new CustomDatePicker(getContext(), s -> tvDate.setText(s), now , end);
            picker.setIsLoop(true);
            picker.showSpecificTime(true);

        }




    @Override
    public int getLayoutRes() {
        return R.layout.add_fragment_orders;

    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void save(Order order) {

    }

    @Override
    public void  onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(!hidden){
            searchListener.hideSearch();
        }
    }
    @Override
    public  void  onAttach(Context context) {
        super.onAttach(context);
        try {
            searchListener = (OnFragmentIntInteractionListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "必须实现OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchListener = null;
        listener = null;
    }

    public interface OnOrderCreatedListener{
        /**
         * 取消添加订单
         */
        void  cancelAddOrder();
        /**
         * 保存订单
         * param order 订单
         */
        void  saveOrder(Order order);
    }
}

