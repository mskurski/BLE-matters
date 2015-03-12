package com.matters.ble.library.manager;

import android.content.ComponentName;
import android.os.Handler;
import android.os.RemoteException;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ServiceController;

import com.matters.ble.library.LibraryRobolectrictTestRunner;

import static com.matters.ble.library.assertions.BLEManagerAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Robolectric.shadowOf_;
import static org.robolectric.Robolectric.application;

@RunWith(LibraryRobolectrictTestRunner.class)
public class BLEManagerTest {

    @Mock
    private BLEManager.ServiceBoundCallback serviceBoundCallback;

    private final ShadowApplication shadowApplication;

    private final BLEManager SUT;

    private final ComponentName componentName;

    private final BLEService backingService;

    public BLEManagerTest() {
        SUT = BLEManager.newInstance(application);

        shadowApplication = shadowOf_(application);

        backingService = spy(ServiceController.of(BLEService.class)
                                    .attach()
                                    .create()
                                    .get());
        componentName = new ComponentName(application, BLEService.class.getName());

        shadowApplication.setComponentNameAndServiceForBindService(componentName, backingService.onBind(null));
    }

    @Before
    public void beforeEachTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldLaunchServiceBoundCallbackThenConnectThenDisconnect() throws RemoteException {
        SUT.connect(serviceBoundCallback);

        assertThat(SUT).isConnected();

        SUT.disconnect();

        assertThat(SUT).isDisconnected();

        verify(serviceBoundCallback, times(1)).onServiceBound();
    }

    @Test
    public void messagingTest() throws RemoteException {
        SUT.connect(serviceBoundCallback);

        ShadowLooper.pauseMainLooper();

        assertThat(SUT).isConnected();

        Handler messagingHandler = backingService.getMessagingHandler();

        SUT.startRanging();

        Assertions.assertThat(messagingHandler.hasMessages(BLEService.MESSAGE_START_RANGING));

        SUT.stopRanging();

        Assertions.assertThat(messagingHandler.hasMessages(BLEService.MESSAGE_STOP_RANGING));

        SUT.disconnect();

        Assertions.assertThat(messagingHandler.hasMessages(BLEService.MESSAGE_DISCONNECT));
    }

}
