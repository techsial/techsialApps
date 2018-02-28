package com.techsial.apps.video_to_mp3.audio_extractor.managers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

public class FacebookManager {

    private Activity mMain;
    //    private OnFacebookProfileResponse onFacebookProfileResponse;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            Toast.makeText(mMain, "Successfully Posted", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(mMain, "Cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(mMain, "Unable to process request. Please try later!", Toast.LENGTH_LONG).show();
        }

    };
    private CallbackManager callbackManager;
    private boolean canPresentShareDialog;
    private String fbName = null;

    public FacebookManager(Activity pMain) {
        mMain = pMain;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void postVideoDialog(final String title, final String description,
                                 final String url, final String imageUrl, final FacebookCallback<ShareDialog.Result> callBackListner) {


        String strShareUrl = "";
//                Constants.URLs.fbShareLinkBaseUrl + "description=" + description + "&title=" + title + "&image=" + imageUrl + "&keydata=" + strKeyData;

//        Uri videoFileUri = null;
//        ShareVideo  shareVideo= new ShareVideo.Builder() .setLocalUrl(videoFileUri) .build();
//        ShareVideoContent content = new ShareVideoContent.Builder() .setVideo(shareVideo) .build();

        final ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://web.facebook.com/techsial"))
                .build();

        if (isLoggedIn()) {
            ShareDialog shareDialog = new ShareDialog(mMain);
            // Can we present the share dialog for regular links?
            canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
            callbackManager = CallbackManager.Factory.create();
            shareDialog.registerCallback(callbackManager,
                    callBackListner);

            Profile profile = Profile.getCurrentProfile();


            if (canPresentShareDialog) {
                shareDialog.show(linkContent);
            } else if (profile != null && hasPublishPermission()) {
                ShareApi.share(linkContent, shareCallback);
            }
        } else {
            loginFacebook(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult result) {
                    Toast.makeText(mMain, "Successfully Logged in!", Toast.LENGTH_LONG).show();

                    ShareDialog shareDialog;
                    // Can we present the share dialog for regular links?
                    canPresentShareDialog = ShareDialog
                            .canShow(ShareLinkContent.class);
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog = new ShareDialog(mMain);
                    shareDialog.registerCallback(callbackManager, callBackListner);

                    Profile profile = Profile.getCurrentProfile();

                    if (canPresentShareDialog) {
                        shareDialog.show(linkContent);
                    } else if (profile != null && hasPublishPermission()) {
                        ShareApi.share(linkContent, shareCallback);
                    }
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(mMain, "Unable to process request. Please try later!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(mMain, "Cancel", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void postStatusDialog(final String title, final String description,
                                 final String url, final String imageUrl, final FacebookCallback<ShareDialog.Result> callBackListner) {


        String strShareUrl = "";
//                Constants.URLs.fbShareLinkBaseUrl + "description=" + description + "&title=" + title + "&image=" + imageUrl + "&keydata=" + strKeyData;

//        Uri videoFileUri = null;
//        ShareVideo  shareVideo= new ShareVideo.Builder() .setLocalUrl(videoFileUri) .build();
//        ShareVideoContent content = new ShareVideoContent.Builder() .setVideo(shareVideo) .build();

        final ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://web.facebook.com/techsial"))
                .build();

        if (isLoggedIn()) {
            ShareDialog shareDialog = new ShareDialog(mMain);
            // Can we present the share dialog for regular links?
            canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
            callbackManager = CallbackManager.Factory.create();
            shareDialog.registerCallback(callbackManager,
                    callBackListner);

            Profile profile = Profile.getCurrentProfile();


            if (canPresentShareDialog) {
                shareDialog.show(linkContent);
            } else if (profile != null && hasPublishPermission()) {
                ShareApi.share(linkContent, shareCallback);
            }
        } else {
            loginFacebook(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult result) {
                    Toast.makeText(mMain, "Successfully Logged in!", Toast.LENGTH_LONG).show();

                    ShareDialog shareDialog;
                    // Can we present the share dialog for regular links?
                    canPresentShareDialog = ShareDialog
                            .canShow(ShareLinkContent.class);
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog = new ShareDialog(mMain);
                    shareDialog.registerCallback(callbackManager, callBackListner);

                    Profile profile = Profile.getCurrentProfile();

                    if (canPresentShareDialog) {
                        shareDialog.show(linkContent);
                    } else if (profile != null && hasPublishPermission()) {
                        ShareApi.share(linkContent, shareCallback);
                    }
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(mMain, "Unable to process request. Please try later!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(mMain, "Cancel", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null
                && accessToken.getPermissions().contains("publish_actions");
    }

    public void inviteFriend(String appLinkUrl, String previewImageUrl,
                             FacebookCallback<AppInviteDialog.Result> callback) {

        callbackManager = CallbackManager.Factory.create();

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl).build();

            AppInviteDialog appInviteDialog = new AppInviteDialog(mMain);

            if (callback != null) {
                appInviteDialog.registerCallback(callbackManager, callback);
            } else {
                appInviteDialog.registerCallback(callbackManager,
                        new FacebookCallback<AppInviteDialog.Result>() {
                            @Override
                            public void onSuccess(AppInviteDialog.Result result) {
                                Log.d("Invitation",
                                        "Invitation Sent Successfully");

                                Toast.makeText(mMain, result.toString(), Toast.LENGTH_LONG).show();
                                // finish();
                            }

                            @Override
                            public void onCancel() {
                                Log.d("Invitation", "cancel");

                                Toast.makeText(mMain, "Cancel", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(FacebookException e) {
                                Log.d("Invitation", "Error Occured");
                                Toast.makeText(mMain, "Unable to process request. Please try later!", Toast.LENGTH_LONG).show();
                            }
                        });
            }

            appInviteDialog.show(content);
        }
    }

    public void checkIn(String placeId, FacebookCallback<ShareDialog.Result> callBackListner) {

        ShareDialog shareDialog;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(mMain);

        ShareContent linkContent = new ShareLinkContent.Builder()
                .setPlaceId(placeId).build();
        shareDialog.registerCallback(callbackManager, callBackListner);
        shareDialog.show(mMain, linkContent);
    }

    public boolean isLoggedIn() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void loginFacebook(
            final FacebookCallback<LoginResult> callBackListner) {

        callbackManager = CallbackManager.Factory.create();

        // publish_actions
        LoginManager.getInstance().logInWithPublishPermissions(mMain, Collections.singletonList("publish_actions"));

        LoginManager.getInstance().registerCallback(callbackManager,
                callBackListner);
    }

    public void loginFacebookWithProfileInformation(
            final FacebookCallback<LoginResult> callBackListner) {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(mMain,
                Arrays.asList("public_profile", "email", "user_birthday", "user_location"));

        LoginManager.getInstance().registerCallback(callbackManager,
                callBackListner);
    }

    public AccessToken getCurrentAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public String getName() {

        new GraphRequest(AccessToken.getCurrentAccessToken(), ""
                + AccessToken.getCurrentAccessToken().getUserId() + "", null,
                HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                        /* handle the result */
                Log.i("dataArray", response.toString());
                JSONObject jsonObject = response.getJSONObject();
                try {
                    fbName = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(mMain, "Unable to process request. Please try later!", Toast.LENGTH_LONG).show();
                }
            }
        }).executeAsync();

        return fbName;
    }

    public void callFacebookLogout() {
        LoginManager.getInstance().logOut();
    }

}