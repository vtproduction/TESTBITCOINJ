package midsummer.com.testbitcoinj

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lambdaworks.jni.Platform
import org.bitcoinj.core.*
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet
import org.spongycastle.asn1.ua.DSTU4145NamedCurves.params
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = "Birdy3In1"
    private lateinit var w : Wallet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seedCode = "city peanut castle similar anchor dove fork frog garlic domain curious tone"
        val seed = DeterministicSeed(seedCode,null,"", 1409478661L)

        val parameters = TestNet3Params.get()
        //w = Wallet.fromSeed(parameters, seed)
        val walletAppKit = object : WalletAppKit(parameters, cacheDir, "wallet_name") {
            override fun onSetupCompleted() {
                wallet().allowSpendingUnconfirmedTransactions()
                setupWalletListeners(wallet())
                Log.d(TAG, "Balance: ${wallet().freshReceiveAddress()} ${wallet().balance.toFriendlyString()}")
            }
        }
        walletAppKit.setDownloadListener(object :DownloadProgressTracker(){
            override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                super.progress(pct, blocksSoFar, date)
                Log.d(TAG, "Download progress $pct")
            }

            override fun doneDownload() {
                super.doneDownload()
                Log.d(TAG, "Download Done")
                Log.d(TAG, "Balance: ${walletAppKit.wallet().freshReceiveAddress()} ${walletAppKit.wallet().balance.toFriendlyString()}")
            }
        })
        walletAppKit.setBlockingStartup(false)
        walletAppKit.startAsync()
        walletAppKit.restoreWalletFromSeed(seed)

    }

    private fun setupWalletListeners(wallet: Wallet) {
        wallet.addCoinsReceivedEventListener { wallet1, tx, prevBalance, newBalance ->
            //view.displayMyBalance(wallet.balance.toFriendlyString())
            Log.d(TAG,"balance: ${wallet.freshReceiveAddress()} ${wallet.balance.toFriendlyString()}")
            if (tx.purpose == Transaction.Purpose.UNKNOWN)
                //view.showToastMessage("Receive " + newBalance.minus(prevBalance).toFriendlyString())
                Log.d(TAG, "Receive: ${newBalance.minus(prevBalance).toFriendlyString()}")
        }
        wallet.addCoinsSentEventListener { wallet12, tx, prevBalance, newBalance ->
            Log.d(TAG,"balance2: ${wallet.freshReceiveAddress()} ${wallet.balance.toFriendlyString()}")
            Log.d(TAG, "Sent: ${prevBalance.minus(newBalance).minus(tx.fee).toFriendlyString()}")
            /*view.displayMyBalance(wallet.balance.toFriendlyString())
            view.clearAmount()
            view.displayRecipientAddress(null)
            view.showToastMessage("Sent " + prevBalance.minus(newBalance).minus(tx.fee).toFriendlyString())*/
        }
    }
}
