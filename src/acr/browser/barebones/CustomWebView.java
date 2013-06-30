package acr.browser.barebones;

import java.lang.reflect.Method;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

public final class CustomWebView extends WebView {
	private float location;
	private boolean first = false;
    final int API = FinalVars.API;
	final boolean showFullScreen = Barebones.showFullScreen;
	final View uBar = Barebones.uBar;
	final Animation slideUp = Barebones.slideUp;
	final Animation slideDown = Barebones.slideDown;
	private ZoomButtonsController zoomControl;
	public CustomWebView(Context context) {
		super(context);
		getControls();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(API<11&&zoomControl!=null){
			zoomControl.getZoomControls().setVisibility(View.INVISIBLE);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			first = true;
			if (API <= 10 && !this.hasFocus()) {
				this.requestFocus();
			}
			location = event.getY();
			break;
		}
		case MotionEvent.ACTION_UP:{
			if (showFullScreen&&first) {
				if (uBar.isShown()&&this.getScrollY()<5) {
					uBar.startAnimation(slideUp);
				} else if (event.getY()>location && !uBar.isShown()) {
					uBar.startAnimation(slideDown);
				} else if (event.getY()<location && uBar.isShown()){
					uBar.startAnimation(slideUp);
				}
				first = false;
			}
			break;
		}
		}

		return super.onTouchEvent(event);
	}
	private void getControls() {
		if(API<11){
        try {
            Class<?> webview = Class.forName("android.webkit.WebView");
            Method method = webview.getMethod("getZoomButtonsController");
            zoomControl = (ZoomButtonsController) method.invoke(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
		}
    }
}
