package net.lzzy.cinemanager.fragments;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.constants.DbConstants;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.utils.AppUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.SqlRepository;
import net.lzzy.sqllib.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static net.lzzy.cinemanager.utils.ViewUtils.*;

/**
 * Created by lzzy_gxy on 2019/3/26.
 * Description:
 */
public class OrdersFragment extends BaseFragment {


    private AddCinemasFragment.OnFragmentIntInteractionListener listener;
    private static  OrdersFragment instantiate;
    private SqlRepository<Order> repository;

    private float touchX1;
    private Order order;
    private  boolean isDelete = false;

    private GenericAdapter<Order> adapter;
    private float MIN_DISTANCE;
    private List<Order> orders;
    private OrdersFragment factory=OrdersFragment.getInstance();


    private static OrdersFragment getInstance() {
        if (instantiate == null) {
            synchronized (OrdersFragment.class) {
                if (instantiate == new OrdersFragment()) {
                    ;
                }
            }
        }
        return instantiate;
    }
    public List<Order> get(){
        return repository.get();
    }
    public List<Order> searchOrders(String kw){
        try {
            List<Order> orders = null;
            try {
                orders = repository.getByKeyword(kw,new String[]{Order.COL_MOVIE,
                        Order.COL_PRICE,Order.COL_MOVIE_TIME},false);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }
            List<Cinema> cinemas = CinemaFactory.getInstance().searchCinemas(kw);
            if(cinemas.size()>0){
                for(Cinema cinema:cinemas){
                    List<Order> cOrders = null;
                    try {
                        cOrders = repository.getByKeyword(cinema.getId().toString(),
                                new String[]{Order.COL_CINEMA_ID},true);
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    }
                    orders.addAll(cOrders);
                }
            }
            return orders;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public OrdersFragment() {
        repository =new SqlRepository<>(AppUtils.getContext(),Order.class, DbConstants.packager);

    }
    public OrdersFragment(Order order){
        this.order = order;
    }
    @Override
    protected void populate() {
        ListView lv =find(R.id.activity_main_lv);
        View empty = find(R.id.activity_main_tv_none);
        lv.setEmptyView(empty);
        orders = factory.get();
        adapter = new GenericAdapter<Order>(getContext(),R.layout.cinemas_item, (List<Order>) orders) {

            @Override
            public void populate(ViewHolder holder, Order order) {

                String location = String.valueOf(CinemaFactory.getInstance()
                        .getById(order.getCinemaId().toString()));

                holder.setTextView(R.id.cinema_item_tv_name, order.getMovie())
                        .setTextView(R.id.cinema_item_tv_location, location);
                Button btn = holder.getView(R.id.cinema_item_btn);
                btn.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                        .setTitle("确认删除")
                        .setMessage("要删除订单吗")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", (dialog, i)-> {

                                adapter.remove(order);})
                .show());
                int visble = isDelete?View.VISIBLE:View.GONE;
                btn.setVisibility(visble);
                holder.getConvertView().setOnTouchListener(new AbstractTouchHandler() {
                    @Override
                    public boolean handleTouch(MotionEvent event) {
                        slideToDelete(event, btn, order);
                        return true;
                    }
                });


            }

            @Override
            public boolean persistInsert(Order order) {
                return false;
            }

            @Override
            public boolean persistDelete(Order order) {
                return false;
            }
        };




    }

    private void slideToDelete(MotionEvent event, Button btn, Order order) {
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
                touchX1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float touchX2 = event.getX();
                if (touchX1 - touchX2 > MIN_DISTANCE) {
                    if (!isDelete) {
                        btn.setVisibility(View.VISIBLE);
                        isDelete = true;

                    }
                }else {
                    if (btn.isShown()){
                        btn.setVisibility(View.GONE);
                        isDelete =false;
                    }else {
                        clickOrder(order);
                    }

                }
                break;
            default:
                break;
        }

    }
    private void   clickOrder(Order order){
        Cinema cinema =CinemaFactory.getInstance().getById(order
                .getCinemaId().toString());

        String content ="["+ order.getMovie()+"]" + order.getMovieTime() +
               "\n" + cinema.toString() +"票价" + order.getPrice() + "元" ;
        View view =LayoutInflater.from(getContext()).inflate(R.layout.dialog_qrcode,null);
        ImageView img= view.findViewById(R.id.dialog_qrcode_img);
        img.setImageBitmap(AppUtils.createQRCodeBitmap(content,300,300));
        new AlertDialog.Builder(getContext())
                .setView(view).show();





    }
    @Override
    public void  save(Order order){
        adapter.add(order);
    }

    /**

     */
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_orders;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

}
