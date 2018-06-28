# ViewPager+Fragment问题

## Fragment数据刷新问题解救额方案
1. Adapter继承FragmentStatePagerAdapter,这个Adapter需要重写getItemPosition方法，返回POSITION_NONE.然后在activity中的onResume()方法中进行，adapter.notifyDataSetChanged()
