package ro.pub.cs.systems.eim.lab05.startedservice

import android.content.Context
import android.content.Intent
import android.provider.Contacts
import android.util.Log

class ProcessingThread(private val context: Context) : Thread() {

    override fun run() {
        Log.d(Constants.TAG, "Thread.run() was invoked, PID: ${android.os.Process.myPid()} TID: ${android.os.Process.myTid()}")
        while (true) {
            sendMessage(Constants.MESSAGE_STRING)
            sleepThread()
            sendMessage(Constants.MESSAGE_INTEGER)
            sleepThread()
            sendMessage(Constants.MESSAGE_ARRAY_LIST)
        }
    }

    private fun sleepThread() {
        try {
            sleep(Constants.SLEEP_TIME)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun sendMessage(messageType: Int) {
        val intent = Intent()

        when (messageType) {
            Constants.MESSAGE_STRING -> {
                intent.action = Constants.ACTION_STRING
                intent.putExtra(Constants.DATA, Constants.STRING_DATA)
            }

            Constants.MESSAGE_INTEGER -> {
                intent.action = Constants.ACTION_INTEGER
                intent.putExtra(Constants.DATA, Constants.INTEGER_DATA)
            }

            Constants.MESSAGE_ARRAY_LIST -> {
                intent.action = Constants.ACTION_ARRAY_LIST
                intent.putExtra(Constants.DATA, Constants.ARRAY_LIST_DATA)
            }

        }
        val packagename = "ro.pub.cs.systems.eim.lab05.startedserviceactivity"
        intent.setPackage(packagename)
        Log.d(Constants.TAG, "Sending broadcast with action: ${intent.action} to package: $packagename")
        context.sendBroadcast(intent)
    }
}