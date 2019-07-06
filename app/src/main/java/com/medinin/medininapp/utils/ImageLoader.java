package com.medinin.medininapp.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.medinin.medininapp.Myapp;
import com.medinin.medininapp.R;


/**
 * Created by kalyan pvs on 28-Sep-16.
 */

public class ImageLoader {

    public static String LOCAL_FILE_PREFIX = "file://";

    private static boolean isGif(String isGif) {
        if (isGif != null && isGif.endsWith(".gif"))
            return true;
        else
            return false;
    }

    public static void loadImage(String imagePath, ImageView imageView, int placeHolder) {
        loadImageIntrnl(imagePath, imageView, placeHolder);
    }

    public static void loadImage(String imagePath, final ImageView imageView, final View progressView) {
        loadImageIntrnl(imagePath, imageView, progressView, R.drawable.ic_round_user_icon);
    }


    public static void loadCircularImage(String imagePath, final ImageView imageView, int placeHolder) {
        loadImageIntrnl(imagePath, imageView, placeHolder);
    }


    public static void loadCircularImage(String imagePath, final ImageView imageView, final View progressView) {
        loadImageIntrnl(imagePath, imageView, progressView, R.drawable.ic_round_user_icon);
    }

    public static void loadLocalImage(String imagePath, ImageView imageView, int placeHolder) {
        Glide.with(Myapp.getContext()).load(imagePath)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(imageView);
    }

    private static void loadImageIntrnl(String imagePath, ImageView imageView, int placeHolder) {
        try {
            if (TextUtils.isEmpty(imagePath)) {
                imageView.setImageResource(placeHolder);
            } else {
                if (isGif(imagePath)) {
                    Glide.with(Myapp.getContext()).load(imagePath)
                            .placeholder(placeHolder)
                            .error(placeHolder)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .dontAnimate()
                            .into(imageView);
                } else {
                    //                Picasso.with(imageView.getContext())
                    //                        .load(imagePath)
                    //                        .placeholder(placeHolder)
                    //                        .error(placeHolder)
                    //                        .into(imageView);

                    Glide.with(Myapp.getContext()).load(imagePath)
                            .placeholder(placeHolder)
                            .error(placeHolder)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .dontAnimate()
                            .into(imageView);

                    //            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    //                    .showImageOnLoading(placeHolder).showImageForEmptyUri(placeHolder)
                    //                    .showImageOnFail(placeHolder).cacheInMemory(true)
                    //                    .displayer(new SimpleBitmapDisplayer()).cacheOnDisk(true)
                    //                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    //                    .bitmapConfig(Bitmap.FileConfig.RGB_565).build();
                    //            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imagePath, imageView, options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void loadImageIntrnl(String imagePath, final ImageView imageView, final View progressView, int placeholder) {
        try {
            if (TextUtils.isEmpty(imagePath)) {
                imageView.setImageResource(placeholder);
                progressView.setVisibility(View.GONE);
            } else {
                progressView.setVisibility(View.VISIBLE);
                //        Ion.with(imageView.getContext())
                //                .load(imagePath)
                //                .withBitmap()
                //                .placeholder(placeholder)
                //                .error(placeholder)
                //                .intoImageView(imageView).setCallback(new FutureCallback<ImageView>() {
                //            @Override
                //            public void onCompleted(Exception e, ImageView result) {
                //                progressView.setVisibility(View.GONE);
                //            }
                //        });

                Glide.with(Myapp.getContext()).load(imagePath)
                        .placeholder(placeholder).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                        progressView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressView.setVisibility(View.GONE);
                        return false;
                    }
                })
                        .error(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .dontAnimate()
                        .into(imageView);

                //            Picasso.with(imageView.getContext())
                //                    .load(imagePath)
                //                    .placeholder(placeholder)
                //                    .error(placeholder)
                //                    .into(new Target() {
                //                        @Override
                //                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //                            imageView.setImageBitmap(bitmap);
                //                            progressView.setVisibility(View.GONE);
                //                        }
                //
                //                        @Override
                //                        public void onBitmapFailed(Drawable errorDrawable) {
                //                            imageView.setImageDrawable(errorDrawable);
                //                            progressView.setVisibility(View.GONE);
                //                        }
                //
                //                        @Override
                //                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                //                            imageView.setImageDrawable(placeHolderDrawable);
                //                            progressView.setVisibility(View.GONE);
                //                        }
                //                    });

                //        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //                .cacheInMemory(true).considerExifParams(true)
                //                .displayer(new SimpleBitmapDisplayer())
                //                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheOnDisk(true)
                //                .bitmapConfig(Bitmap.FileConfig.RGB_565).build();
                //        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imagePath, imageView, options, new ImageLoadingListener() {
                //            @Override
                //            public void onLoadingStarted(String s, View view) {
                //
                //            }
                //
                //            @Override
                //            public void onLoadingFailed(String s, View view, FailReason failReason) {
                //                progressView.setVisibility(View.GONE);
                //            }
                //
                //            @Override
                //            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                //                progressView.setVisibility(View.GONE);
                //            }
                //
                //            @Override
                //            public void onLoadingCancelled(String s, View view) {
                //                progressView.setVisibility(View.GONE);
                //            }
                //        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public static void loadImage(String imagePath, final ImageView imageView, int placeholder, final ImageLoadCallback callback) {
//        {
//            try {
//                if (TextUtils.isEmpty(imagePath)) {
//                    imageView.setImageResource(placeholder);
//                    if (callback != null) {
//                        callback.onImageLoaded();
//                    }
//                } else {
//                    Glide.with(MyApplication.getContext()).load(imagePath)
//                            .placeholder(placeholder).listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (callback != null) {
//                                callback.onImageLoaded();
//                            }
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            if (callback != null) {
//                                callback.onImageLoaded();
//                            }
//                            return false;
//                        }
//                    })
//                            .error(placeholder)
//                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                            .dontAnimate()
//                            .into(imageView);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
