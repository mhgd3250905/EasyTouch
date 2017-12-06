package com.skkk.easytouch.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 创建于 2017/8/14
 * 作者 admin
 */
/*
* 
* 描    述：RecyclerView数据适配器基类
* 作    者：ksheng
* 时    间：2017/8/14$ 21:01$.
*/
public abstract class BaseAdapter<T,V extends BaseViewHolder> extends RecyclerView.Adapter<V>{
    protected List<T> mDataList;
    protected Context context;
    protected OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int pos);
    }

    public BaseAdapter(Context context, List<T> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        V v=getCostumViewHolder(parent,viewType);
        return v;
    }

    @Override
    public void onBindViewHolder(final V holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v,holder.getAdapterPosition());
                }
            }
        });
        setViewHolder(holder,position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 返回需要设置的ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract V getCostumViewHolder(ViewGroup parent, int viewType);

    /**
     * 设置ViewHolder绑定事件
     * @param holder
     * @param position
     */
    protected abstract void setViewHolder(V holder, int position);

    /**
     * 添加单个数据
     * @param t
     */
    public void add(T t){
        mDataList.add(t);
    }

    /**
     * 添加指定位置的单个数据
     * @param index
     * @param t
     */
    public void add(int index,T t){
        mDataList.add(index,t);
    }

    /**
     * 批量添加数据
     * @param list
     */
    public void addAll(List<T> list){
        mDataList.addAll(list);
    }

    /**
     * 在指定位置批量添加数据
     * @param index
     * @param list
     */
    public void addAll(int index,List<T> list){
        mDataList.addAll(index,list);
    }

    /**
     * 删除单个数据
     * @param t
     */
    public void remove(T t){
        mDataList.remove(t);
    }

    /**
     * 删除指定位置单个数据
     * @param index
     */
    public void remove(int index){
        mDataList.remove(index);
    }

    /*
    * 设置数据集
    * */
    public List<T> getmDataList() {
        return mDataList;
    }

    public void setmDataList(List<T> mDataList) {
        this.mDataList = mDataList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
