package weloveclouds.ecs.configuration.providers;

/**
 * Created by Benoit on 2016-12-21.
 */
public class NotificationServiceConfigurationProvider {
    private static final int NOTIFICATION_SERVICE_PORT = 25000;

    public static int getNotificationServicePort() {
        return NOTIFICATION_SERVICE_PORT;
    }
}
