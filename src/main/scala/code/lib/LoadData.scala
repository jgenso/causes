package code.lib


import code.model._
import net.liftweb.common._
import net.liftweb.record.field.Countries
import org.bson.types.ObjectId

/**
 * Created by andrea on 12/6/14.
 */
object LoadData extends Logger {

  def loadUsers() = {
    loadUser("Tatiana Moruno", "","tatiana.moruno.ro@gmail.com", "tatianam", "America/Chicago", Countries.USA, "en_US",
      "77420087", false,"12345")

    loadUser("Juan Jose Olivera", "","jota.jota.or@gmail.com", "j2", "America/Chicago", Countries.USA, "en_US",
      "75956624", false,"12345")

    loadUser("Marcelo Rodriguez Claros", "","marcelo.rodriguezc@gmail.com", "marcelorodcla", "America/Chicago", Countries.USA, "en_US",
      "72278363", false,"12345")

    loadUser("Azul Camila Moruno", "","azul@gmail.com", "azul", "America/Chicago", Countries.USA, "en_US",
      "77420087", false,"12345")

    loadUser("Violeta Rodriguez", "","violeta@gmail.com", "violeta", "America/Chicago", Countries.USA, "en_US",
      "77420088", false,"12345")

    loadUser("Camila Olivera", "","camih@gmail.com", "camih", "America/Chicago", Countries.USA, "en_US",
      "77420089", false,"12345")

    loadUser("Henry Vargas Ovando", "","vargasho@gmail.com", "molle", "America/Chicago", Countries.USA, "en_US",
      "77420090", false,"12345")
  }

  def loadCauses() = {
    val date = java.util.Calendar.getInstance()
    date.set(java.util.Calendar.YEAR, 2014)
    date.set(java.util.Calendar.MONTH, 11)
    date.set(java.util.Calendar.DAY_OF_MONTH, 6)

    val startCoorDate = java.util.Calendar.getInstance()
    startCoorDate.set(java.util.Calendar.YEAR, 2014)
    startCoorDate.set(java.util.Calendar.MONTH, 11)
    startCoorDate.set(java.util.Calendar.DAY_OF_MONTH, 6)

    val endCoorDate = java.util.Calendar.getInstance()
    endCoorDate.set(java.util.Calendar.YEAR, 2014)
    endCoorDate.set(java.util.Calendar.MONTH, 11)
    endCoorDate.set(java.util.Calendar.DAY_OF_MONTH, 6)

    val startExeDate = java.util.Calendar.getInstance()
    startExeDate.set(java.util.Calendar.YEAR, 2014)
    startExeDate.set(java.util.Calendar.MONTH, 11)
    startExeDate.set(java.util.Calendar.DAY_OF_MONTH, 6)

    val endExeDate = java.util.Calendar.getInstance()
    endExeDate.set(java.util.Calendar.YEAR, 2014)
    endExeDate.set(java.util.Calendar.MONTH, 11)
    endExeDate.set(java.util.Calendar.DAY_OF_MONTH, 6)

    val azul = User.findByUsername("azul").getOrElse(User.createRecord)
    val violeta = User.findByUsername("violeta").getOrElse(User.createRecord)
    val camila = User.findByUsername("camih").getOrElse(User.createRecord)
    val j2 = User.findByUsername("j2").getOrElse(User.createRecord)
    val marcelo = User.findByUsername("marcelorodcla").getOrElse(User.createRecord)
    val tatiana = User.findByUsername("tatianam").getOrElse(User.createRecord)
    val henry = User.findByUsername("molle").getOrElse(User.createRecord)


    // cause 1
    val cause1 = Cause.createRecord

    cause1.name("Let's help Miriam and her 2 children")
    cause1.organizer(henry.id.get)
    cause1.slogan("Miriam we need! join now!")
    cause1.description("Miriam Veizaga and her 2 young children are seriously injured, " +
      " with burns of second and third degree due to a gas leak in her home, in the town of Vinto." +
      " Fortunately neighbors were organize to assist them, managed to transfer to pediatric hospital." +
      "Now this family need our help." +
      "Acording to medical diagnosis. Miriam suffered burns of second and third degree in 70% of her body." +
      "Her son Jose, is very serious state in intensive care with 90% of the bosy burned. Abigail, the smallest" +
      " has burns in 70% of her body. This family is poor and need our help." +
      "Parcipate by joinning  the cause or get information to someone you think can help us!")

    cause1.registerDate(date.getTime)
    cause1.status(CauseStatus.Active)
    cause1.startCoorDate(startCoorDate.getTime)
    cause1.endCoorDate(endCoorDate.getTime)
    cause1.startExeDate(startExeDate.getTime)
    cause1.endExeDate(endExeDate.getTime)
    cause1.country(Countries.USA)
    cause1.location("Maine")
    cause1.tags(List("children", "medical assitance"))

    cause1.isInmedCoor(true)
    cause1.isInmedExe(true)
    cause1.organizer(User.findAll.headOption.map(_.id.get) getOrElse ObjectId.get())

    cause1.saveBox() match {
      case Empty => error("Cause 2 not saved!")
      case Failure(msg, _, _) => error("Cause 2 not saved!")
      case Full(cause: Cause) => {
        info("Cause 1 saved")
        loadResource(cause,"Quemacurán cream", 60, "unit of 500 mg.")
        loadResource(cause,"Albumin", 60, "unit of 500 gr.")
        loadResource(cause,"Soothing", 60, "tablet 50 mg")
        loadResource(cause,"Internment by day", 500, "Bs.")

        // followers
        loadCauseFollower(cause, azul,true, true)
        loadCauseFollower(cause, violeta, true, true)
        loadCauseFollower(cause, camila, true, true)

        // joined
        val cream = Resource.findByName("Quemacurán cream").headOption.map(r => r).getOrElse(Resource.createRecord)
        loadCommittedResource(cause: Cause, j2, cream, 15, CommittedResourceStatus.Executed, date.getTime)

        val soothing = Resource.findByName("Soothing").headOption.map(r => r).getOrElse(Resource.createRecord)
        loadCommittedResource(cause, marcelo, soothing, 10, CommittedResourceStatus.Committed, date.getTime)

        // updates
        loadUpdate(cause, cause.organizer.obj.getOrElse(User.createRecord),"Do not be indifferent, Miriam needs us!",
          "Miriam is going a very difficult situation right now, she lives alone and is in the care of their young " +
            " children, she do not have an own home  and before the accident she worked as a street vendor. " +
            " Her first words were to regain consciousness was \"Please help my children.\" We can not remain " +
            " indifferent to this situation, join the cause now!", date.getTime)

        loadUpdate(cause, cause.organizer.obj.getOrElse(User.createRecord),"Miriam health worsens",
          "With only 23 years, she's going a very difficult stage, his father Juan says Miriam's health is getting worse," +
            " but they have hope, she asks for her children and thanked all those who so far have offered their help", date.getTime)

        loadUpdate(cause, cause.organizer.obj.getOrElse(User.createRecord),"Abigail was successfully recovered.",
          "The sweet little Abigail is successfully recovering, thanks to good medical monitoring and support of people." +
            " She's out of intensive care and a group of volunteers is processed on a journey to reconstructive surgery " +
            " of his burns. Thank you!", date.getTime)
      }
    }

    // cause 2
    val cause2 = Cause.createRecord
    cause2.name("Construction of houses for stray dogs")
    cause2.organizer(camila.id.get)
    cause2.slogan("I ALSO NEED A ROOF SHELTER ME OF COLD, WILL I BE MAKING A SMALL HELPS? ")
    cause2.description("Have you ever given even thought to how cold it gets at night?. Can you imagine what it's like " +
      "on the streets with nowhere shelter?. You are not the only one who needed shelter and protection ... " +
      "The street life is very hard, they are cold, hungry and needs that you cannot imagine." +
      "IT IS TIME TO HELP!" +
      "Because we work with recycled material suchas as pallets (wood), and pc-monitor"
    )

    cause2.registerDate(date.getTime)
    cause2.status(CauseStatus.Active)
    cause2.startCoorDate(startCoorDate.getTime)
    cause2.endCoorDate(endCoorDate.getTime)
    cause2.startExeDate(startExeDate.getTime)
    cause2.endExeDate(endExeDate.getTime)
    cause2.country(Countries.Australia)
    cause2.location("Sydney")
    cause2.tags(List("dogs", "animals", "house"))

    cause2.isInmedCoor(false)
    cause2.isInmedExe(false)
    cause2.organizer(User.findAll.headOption.map(_.id.get) getOrElse ObjectId.get())

    cause2.saveBox() match {
      case Empty => error("Cause 2 not saved!")
      case Failure(msg, _, _) => error("Cause 2 not saved!")
      case Full(cause: Cause) => {
        info("Cause 2 saved")

        loadResource(cause,"Hammer", 10, "")
        loadResource(cause,"Kickstand", 3, "")
        loadResource(cause,"Pliers", 10, "")
        loadResource(cause,"Gloves",20, "pair")

        loadResource(cause,"Screwdriver",8, "")
        loadResource(cause,"Spray paint and metal",5, "1Liter pot")
        loadResource(cause,"Rubber",5, "Meter")
        loadResource(cause,"Trimmings",-1, "")

        loadResource(cause,"Pallet or wood",30, "m2")
        loadResource(cause,"Chains",20, "Meter")
        loadResource(cause,"padlocks to strengthen the houses on poles.",20, "")
        loadResource(cause,"old blankets",40, "")


        // followers
        loadCauseFollower(cause, azul,true, true)
        loadCauseFollower(cause, violeta, true, true)
        loadCauseFollower(cause, tatiana, true, true)

        // joined
        val pallet = Resource.findByName("Pallet or wood").headOption.map(r => r).getOrElse(Resource.createRecord)
        loadCommittedResource(cause: Cause, j2, pallet, 3, CommittedResourceStatus.Committed, date.getTime)

        val screwdriver = Resource.findByName("Screwdriver").headOption.map(r => r).getOrElse(Resource.createRecord)
        loadCommittedResource(cause, marcelo, screwdriver, 5, CommittedResourceStatus.Executed, date.getTime)

      }
    }

  }

  private def loadUser(name:String, location: String, email: String, username: String,
                        timeZone:String, country: Countries.I18NCountry, locale: String, cellPhone: String,
                        verified: Boolean, password: String) = {
    val user = User.createRecord
    user.name(name)
    user.location(location)
    user.email(email)
    user.username(username)
    user.timezone(timeZone)
    user.country(country)
    user.locale(locale)
    user.cellPhone(cellPhone)
    user.verified(verified)
    user.password(password, true)

    user.saveBox() match {
      case Empty => error("User " + user.name.get + " not saved!")
      case Failure(msg, _, _) => error("User " + user.name.get + " not saved! " + msg)
      case Full(_) => info("User " + user.name.get + " saved")
    }
  }

  private def loadResource(cause: Cause, name: String, quantity: Int, unit: String) = {
    val resource = Resource.createRecord
    resource.name(name)
    resource.quantity(quantity)
    resource.unit(unit)
    resource.cause(cause.id.get)

    resource.saveBox() match {
      case Empty => error("Resource " + resource.name.get + " not saved!")
      case Failure(msg, _, _) => error("Resource " + resource.name.get + " not saved! " + msg)
      case Full(_) => info("Resource " + resource.name.get + " saved")
    }
  }

  private def loadCauseFollower(cause: Cause, follower: User, receiptEmail: Boolean, receiptSms: Boolean) = {
    val causeFollower = CauseFollower.createRecord
    causeFollower.receiptEmail(receiptEmail)
    causeFollower.receiptSms(receiptSms)
    causeFollower.cause(cause.id.get)
    causeFollower.follower(follower.id.get)

    causeFollower.saveBox() match {
      case Empty => error("Followers of cause  not saved!")
      case Failure(msg, _, _) => error("Followers of cause  not saved! " + msg)
      case Full(_) => info("Followers of cause saved")
    }
  }

  private def loadCommittedResource(cause: Cause, joined: User, resource: Resource, quantity: Int,
                                    status: CommittedResource.status.MyType, date: java.util.Date) = {
    val commitedResource = CommittedResource.createRecord
    commitedResource.cause(cause.id.get)
    commitedResource.joinedUser(joined.id.get)
    commitedResource.resource(resource.id.get)
    commitedResource.quantity(quantity)
    commitedResource.status(status)
    commitedResource.registerDate(date)

    commitedResource.saveBox() match {
      case Empty => error("Commited resource of cause  not saved!")
      case Failure(msg, _, _) => error("Commited resource of cause  not saved! " + msg)
      case Full(_) => info("Commited resource of cause saved")
    }
  }

  private def loadUpdate(cause: Cause, user: User,  title: String, description: String, date: java.util.Date) = {
    val update = News.createRecord
    update.title(title)
    update.description(description)
    update.registerDate(date)
    update.cause(cause.id.get)
    update.user(user.id.get)

    update.saveBox() match {
      case Empty => error("News of cause  not saved!")
      case Failure(msg, _, _) => error("News of cause  not saved! " + msg)
      case Full(_) => info("News of cause saved")
    }
  }

}
