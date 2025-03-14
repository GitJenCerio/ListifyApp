import com.google.firebase.firestore.FirebaseFirestore
import com.jencerio.listifyapp.dao.ShoppingReminderDao
import com.jencerio.listifyapp.model.ShoppingReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ShoppingReminderRepository(private val shoppingReminderDao: ShoppingReminderDao?) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("shopping_reminders")

    fun getSShoppingReminder(): Flow<List<ShoppingReminder>> {
        return shoppingReminderDao?.getAll() ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun addShoppingReminder(shoppingReminder: ShoppingReminder) {
        shoppingReminderDao?.insert(shoppingReminder) ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun updateShoppingReminder(shoppingReminder: ShoppingReminder) {
        shoppingReminderDao?.update(shoppingReminder) ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun deleteShoppingReminder(shoppingReminder: ShoppingReminder) {
        shoppingReminderDao?.delete(shoppingReminder) ?: throw IllegalStateException("Local DB not initialized")
    }

// 🔥 Firebase Firestore CRUD Methods 🔥

    suspend fun addShoppingReminderToFirestore(shoppingReminder: ShoppingReminder) {
        collectionRef.document(shoppingReminder.id).set(shoppingReminder).await()
    }

    suspend fun updateShoppingReminderInFirestore(shoppingReminder: ShoppingReminder) {
        collectionRef.document(shoppingReminder.id).set(shoppingReminder).await()
    }

    suspend fun deleteShoppingReminderFromFirestore(shoppingReminder: ShoppingReminder) {
        collectionRef.document(shoppingReminder.id).delete().await()
    }

    suspend fun getShoppingReminderFromFirestore(id: String): ShoppingReminder? {
        val documentSnapshot = collectionRef.document(id).get().await()
        return documentSnapshot.toObject(ShoppingReminder::class.java)
    }

}