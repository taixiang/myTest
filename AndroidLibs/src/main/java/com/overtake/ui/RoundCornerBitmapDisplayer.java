package com.overtake.ui;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by kevinhoo on 14-3-19.
 */
public class RoundCornerBitmapDisplayer extends RoundedBitmapDisplayer {

    public RoundCornerBitmapDisplayer(int cornerRadiusPixels) {
        super(cornerRadiusPixels);
    }

    public RoundCornerBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
        super(cornerRadiusPixels, marginPixels);
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        if (bitmap.getWidth() < imageAware.getWidth() || bitmap.getHeight() < imageAware.getHeight()) {
            Point size = CircleBitmapDisplayer.findProperSize(bitmap, imageAware);
            bitmap = Bitmap.createScaledBitmap(bitmap, size.x, size.y, false);
        } else if (bitmap.getHeight() > imageAware.getHeight() || bitmap.getWidth() > imageAware.getWidth()) {
            Point size = CircleBitmapDisplayer.findProperSize(bitmap, imageAware);
            bitmap = Bitmap.createScaledBitmap(bitmap, size.x, size.y, false);
        }
        super.display(bitmap, imageAware, loadedFrom);
    }

}
