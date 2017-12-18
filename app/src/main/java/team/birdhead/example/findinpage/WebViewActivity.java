package team.birdhead.example.findinpage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private ActionModeController mActionModeController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);

        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        mWebView = findViewById(R.id.web_view);
        mActionModeController = new ActionModeController(mWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(getIntent().getData().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_web_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.find_in_page) {
            return false;
        }

        mActionModeController.startActionMode();

        return true;
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }

        super.onBackPressed();
    }

    private class ActionModeController implements ActionMode.Callback, WebView.FindListener, TextWatcher, TextView.OnEditorActionListener {

        private ActionMode mActionMode;
        private EditText mQueryView;
        private TextView mCountView;
        private int mDefaultTextColor;

        ActionModeController(WebView webView) {
            webView.setFindListener(this);
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    stopActionMode();
                    return false;
                }
            });
        }

        void startActionMode() {
            if (mActionMode == null) {
                mActionMode = startSupportActionMode(this);
            }
        }

        void stopActionMode() {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;

            getMenuInflater().inflate(R.menu.activity_web_view_find_in_page, menu);

            final View view = getLayoutInflater().inflate(R.layout.acitivity_web_view_cab, null);
            mQueryView = view.findViewById(R.id.query);
            mCountView = view.findViewById(R.id.count);
            mDefaultTextColor = mCountView.getCurrentTextColor();
            mQueryView.addTextChangedListener(this);
            mQueryView.setOnEditorActionListener(this);

            mode.setCustomView(view);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.previous:
                    mWebView.findNext(false);
                    return true;
                case R.id.next:
                    mWebView.findNext(true);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mWebView.clearMatches();

            mActionMode = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int before) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mWebView.findAllAsync(s.toString());
            mCountView.setVisibility(count != 0 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mWebView.findNext(true);
                return true;
            }

            return false;
        }

        @Override
        public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
            if (isDoneCounting) {
                mCountView.setTextColor(numberOfMatches != 0 ? mDefaultTextColor : Color.RED);
                mCountView.setText(getString(R.string.count, numberOfMatches == 0 ? activeMatchOrdinal : activeMatchOrdinal + 1, numberOfMatches));
            }
        }
    }
}
