package br.com.victorlsn.flash.models

import br.com.victorlsn.flash.domain.APIController
import br.com.victorlsn.flash.domain.RetrofitInterface
import br.com.victorlsn.flash.domain.callback
import br.com.victorlsn.flash.interfaces.MainActivityMVP

/**
 * Created by victorlsn on 18/03/19.
 *
 */


class MainActivityModelImp(private val presenter: MainActivityMVP.Presenter) : MainActivityMVP.Model {
    private var apiService = APIController.getInstance(null).getRetrofitInterface()

    // Used for testing

    fun attachRetrofitInstance(retrofitService : RetrofitInterface) {
        apiService = retrofitService
    }

    override fun getVehicles() {
        apiService.getVehiclesList().enqueue(callback { response, error, throwable ->

            response.let {
                val scooterList = response?.body()
                if (scooterList != null) {
                    presenter.requestVehiclesSuccessfully(scooterList)
                }
            }

            error.let {
                if (error != null) {
                    presenter.requestVehiclesFailure(error)
                }
            }

            throwable.let {
                if (throwable != null) {
                    presenter.requestVehiclesFailure("Something happened while trying to retrieve this vehicle's information.\nPlease check your connection or try again later.")
                }
            }
        })
    }

    override fun getVehicleDetails(id: String) {
        apiService.getVehicleDetails(id).enqueue(callback { response, error, throwable ->

            response.let {
                val scooter = response?.body()

                if (scooter != null) {
                    presenter.requestVehicleDetailsSuccessfully(scooter)
                }
            }

            error.let {
                if (error != null) {
                    presenter.requestVehicleDetailsFailure(error)
                }
            }

            throwable.let {
                if (throwable != null) {
                    presenter.requestVehicleDetailsFailure("Something happened while trying to retrieve this vehicle's information.\nPlease check your connection or try again later.")
                }
            }
        })
    }
}
