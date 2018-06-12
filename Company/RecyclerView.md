# RecyclerView的使用

## Adapter基本使用
* 继承RecyclerView.Adapter<T>,T泛型，传入的是自己创建的ViewHolder，所以RecyclerView必须实现ViewHolder
* 实现onCreateViewHolder()和onBindViewHolder()和getItemCount()方法
* onCreateViewHolder():创建ViewHolder，加载布局
* onBindViewHolder()：把数据绑定，填充到相应的itemview中
* getItemCount()：返回数据的数量

## Recycler的初始化
* findViewById()
* 设置布局管理器
```java
LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
ry_record.setLayoutManager(mLayoutManager);
```
* 设置数据适配器Adapter

## 实现点击事件
* RecyclerView没有提供setOnItemClick()方法，需要自己去实现
```java
//1.声明接口
public interface OnItemClickListener {
  void onItemClick(int position);
}

//2.onCreateViewHolder()中实现setOnClickListener()方法
view.setOnClickListener(this);

//3.onBindViewHolder()中实现setTag
holder.itemView.setTag(position);

//4.实现onClick()方法
@Override
public void onClick(View view) {
    if (mItemClickListener != null) {
        mItemClickListener.onItemClick((Integer) view.getTag());
    }
}

//5.提供方法
public void setItemClickListener(OnItemClickListener itemClickListener) {
    mItemClickListener = itemClickListener;
}
```

## 实例
* Adapter
```java
public class RecordListViewAdapter extends RecyclerView.Adapter<RecordListViewAdapter.ViewHolder> implements View.OnClickListener {

    private List<PeopleInfoBean> mList;
    private Context context;
    private OnItemClickListener mItemClickListener;

    public RecordListViewAdapter(Context context, List<PeopleInfoBean> list) {
        this.context = context;
        mList = list;
    }


    /**
     * 声明接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.record_list_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PeopleInfoBean item = mList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (item.getTime() != null) {
            holder.tvTime.setText(sdf.format(new Date((Long.valueOf(item.getTime())))));
        }

        if (item.getName() != null) {
            holder.tvName.setText(item.getName());
        }

        if (item.getResult() != null) {
            holder.tvResult.setText(item.getResult());
        }


        if (item.getProblem() != null) {
            holder.tvProblem.setText(item.getProblem());
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvName;
        TextView tvResult;
        TextView tvProblem;

        private ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvName = itemView.findViewById(R.id.tv_name);
            tvResult = itemView.findViewById(R.id.tv_result);
            tvProblem = itemView.findViewById(R.id.tv_problem);
        }
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick((Integer) view.getTag());
        }
    }
}
```

* Activity
```java
adapter = new RecordListViewAdapter(this, peopleLists);
adapter.setItemClickListener(this);
LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
ry_record.setLayoutManager(mLayoutManager);
ry_record.setAdapter(adapter);
```
