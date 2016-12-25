package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
)

const (
	DeviceJsonPath = "./devices.json"
)

var deviceJson deviceobject

type deviceobject struct {
	Devices []Device `json:"devices"`
}

type Device struct {
	DeviceId     string      `json:"deviceId"`
	DeviceIp     string      `json:"deviceIP"`
	EdgeServerId string      `json:"edgeServerId"`
	EdgeServerIp string      `json:"edgeServerIP"`
	Sensor       string      `json:"sensor"`
	Status       string      `json:"status"`
	Operations   []Operation `json:"operations"`
}

type Operation struct {
	Name string `json:"name"`
}

func handleError(err error) {
	if err != nil {
		fmt.Println("Error : ", err)
	}
}

/*
 * Get device json infor from file
 */
func getDeviceJson() {
	file, err := ioutil.ReadFile(DeviceJsonPath)
	handleError(err)

	json.Unmarshal(file, &deviceJson)
}

/*
 * Handler code for performing different client operations
 * Operations Supported :-
 * 1. Get all devices
 * 2. Add new device
 * 3. Update device info
 * 4. Remove device
 * 5. Update device IP
 * 6. Get device IP
 */
func handler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")

	fmt.Println("New request received : ", r.RequestURI)

	queryMap := r.URL.Query()

	operation := queryMap.Get("operation")

	if operation == "getalldevices" {
		devJson, _ := json.Marshal(deviceJson)
		w.Write(devJson)
	} else if operation == "addnewdevice" {
		var newdevice Device
		devjson := queryMap.Get("devicejson")
		json.Unmarshal([]byte(devjson), &newdevice)
		deviceJson.Devices = append(deviceJson.Devices, newdevice)

	} else if operation == "updatedevice" {
		var updatedevice Device
		devjson := queryMap.Get("devicejson")
		json.Unmarshal([]byte(devjson), &updatedevice)

		for i, element := range deviceJson.Devices {
			if element.DeviceId == updatedevice.DeviceId {
				deviceJson.Devices[i] = deviceJson.Devices[len(deviceJson.Devices)-1]
				deviceJson.Devices = deviceJson.Devices[:len(deviceJson.Devices)-1]

				deviceJson.Devices = append(deviceJson.Devices, updatedevice)
				break
			}

		}

	} else if operation == "removedevice" {
		devid := queryMap.Get("deviceid")

		for i, element := range deviceJson.Devices {
			if element.DeviceId == devid {
				deviceJson.Devices[i] = deviceJson.Devices[len(deviceJson.Devices)-1]
				deviceJson.Devices = deviceJson.Devices[:len(deviceJson.Devices)-1]

				break
			}

		}

	} else if operation == "updatedeviceip" {
		devid := queryMap.Get("deviceid")
		devip := queryMap.Get("deviceip")

		for i, element := range deviceJson.Devices {
			if element.DeviceId == devid {
				deviceJson.Devices[i].DeviceIp = devip
				deviceJson.Devices[i].Status = "LIVE"
				break
			}

		}

	} else if operation == "getdeviceip" {
		devid := queryMap.Get("deviceid")
		var index int

		for i, element := range deviceJson.Devices {
			if element.DeviceId == devid {
				index = i
				break
			}

		}
		w.Write([]byte(deviceJson.Devices[index].DeviceIp))
	}

}

/*
 * Main function
 */
func main() {

	getDeviceJson()

	http.HandleFunc("/", handler)
	http.ListenAndServe(":9000", nil)

}
