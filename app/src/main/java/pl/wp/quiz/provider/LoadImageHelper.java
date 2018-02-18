package pl.wp.quiz.provider;

import pl.wp.quiz.listener.ImageLoadListener;

public class LoadImageHelper {
    public static void downloadImage(ImageLoadListener receiver, String sourceURI) {
        receiver.onImageLoaded(sourceURI);
    }
}
