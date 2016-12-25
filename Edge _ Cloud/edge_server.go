package main

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net"
	"net/http"
	"strconv"
	"time"
)

const (
	Protocol              = "udp"
	DestIpPort            = "192.168.0.100:600"
	HostIpPort            = "192.168.0.103:7777"
	HostIpPort2           = "192.168.0.103:7676"
	HostIpPortReceive     = "192.168.0.103:8888"
	HostIpPortCtrlReceive = "192.168.0.103:9991"
	FuncGetTemp           = "gettemp"
	FuncGetSound          = "gtsound"
	DeviceIdTemp          = 1111
	DeviceIdSound         = 2222
	JsonResponseTemp      = "{\"SensorType\" : \"Temperature\",\"data\" : %s,\"EdgeServer\" : \"EdgeServer1\",\"Device\" : \"BeageBoneBlack-001\", \"status\":\"LIVE\", \"error\" : %d}"
	JsonResponseSound     = "{\"SensorType\" : \"Sound\",\"data\" : \"%s\",\"EdgeServer\" : \"EdgeServer1\",\"Device\" : \"BeageBoneBlack-001\", \"status\":\"LIVE\",\"error\" : %d}"
	JsonResponseSoundTft  = "{\"SensorType\" : \"Sound\",\"data\" : \"%s\",\"EdgeServer\" : \"EdgeServer1\",\"Device\" : \"BeageBoneBlack-001\", \"status\":\"LIVE\",\"error\" : %d}"
	ErrorJson             = "{\"error\" : \"Invalid operation\"}"
	IpUpdateRequest       = "http://192.168.0.103:9000?deviceip=%s:600&operation=updatedeviceip&deviceid=%s"
	IpGetRequest          = "http://192.168.0.103:9000?&operation=getdeviceip&deviceid=%s"
	InitialRequestId      = 1000
	FuncTftOn             = "tftmodeon"
	FuncTftOff            = "tftmodeoff"
	FuncTftModeOn         = "settfon"
	FuncTftModeOff        = "settfof"
	FuncTftTempSend       = "tfttemp"
	DeviceNameTemp        = "TMP-36"
	DeviceNameSound       = "PHANTOM-S1"
)

var ServerAddr *net.UDPAddr
var ServerAddr2 *net.UDPAddr
var ServerAddrReceive *net.UDPAddr
var ServerAddrCtrlReceive *net.UDPAddr
var LocalAddr *net.UDPAddr
var Conn *net.UDPConn
var LocalAddr2 *net.UDPAddr
var Conn2 *net.UDPConn
var ServerConn *net.UDPConn
var CtrlConn *net.UDPConn
var requestId int
var tempResult string
var deviceIp string

/*
 * Display error message for debugging
 */
func handleError(err error) {
	if err != nil {
		fmt.Println("Error : ", err)
	}
}

/*
 * Initialise network connection for Control Server : Edge - Xinu(BBB) communication
 */
func initNetworkConnection() {
	var err error
	ServerAddrCtrlReceive, err = net.ResolveUDPAddr(Protocol, HostIpPortCtrlReceive)
	handleError(err)

	CtrlConn, err = net.ListenUDP(Protocol, ServerAddrCtrlReceive)
	handleError(err)
}

/*
 * Initialise network communication for TFT(Tap for Temperature) mode
 */
func initTftNetworkConf(destipport string) {

	var err error
	ServerAddr2, err = net.ResolveUDPAddr(Protocol, destipport+":600")
	handleError(err)

	LocalAddr2, err = net.ResolveUDPAddr(Protocol, HostIpPort2)
	handleError(err)

	Conn2, err = net.DialUDP(Protocol, LocalAddr2, ServerAddr2)
	handleError(err)

}

/*
 * Intialise network connection for data communication between Xinu(BBB) - Edge
 */
func initUDPConnection(destipport string) {
	var err error

	//Sender UDP Component
	ServerAddr, err = net.ResolveUDPAddr(Protocol, destipport+":600")
	handleError(err)

	LocalAddr, err = net.ResolveUDPAddr(Protocol, HostIpPort)
	handleError(err)

	Conn, err = net.DialUDP(Protocol, LocalAddr, ServerAddr)
	handleError(err)

	//Receiver UDP component
	ServerAddrReceive, err = net.ResolveUDPAddr(Protocol, HostIpPortReceive)
	handleError(err)

	ServerConn, err = net.ListenUDP(Protocol, ServerAddrReceive)
	handleError(err)

}

/*
 * Function for handling communication during TFT(Tap for Temperature) mode
 */
func tftMode() {

	tft_res, _ := callXinuFunction2(FuncGetTemp, DeviceNameTemp)

	tempResult = tft_res

	Conn.Close()
	ServerConn.Close()
	initUDPConnection(deviceIp)

	callXinuFunction(FuncTftTempSend, DeviceNameSound)

}

/*
 * Receiver for receiving function call response over UDP
 */
func receiveXinuResponse(reqId int, devId int) (string, int) {

	if ServerConn != nil {
		defer ServerConn.Close()
	}
	var result string
	var errInt int
	buf := make([]byte, 1024)

	n, addr, err := ServerConn.ReadFromUDP(buf)
	fmt.Println("Received ", string(buf[0:n]), " from ", addr)
	handleError(err)

	rId, _ := strconv.Atoi(string(buf[:4]))

	fmt.Println("RID :", rId, "REQID:", reqId)

	result = string(buf[4:8])

	if rId == reqId || result == "9999" {
		if result == "9999" {
			tftMode()
		}
		errInt = 0
	} else {
		result = "-1"
		errInt = 1
	}

	return result, errInt
}

/*
 * Calls Xinu for getting function result over UDP
 */
func callXinuFunction2(funcName string, deviceId string) (string, int) {
	defer Conn2.Close()

	fmt.Println("Calling Xinu function : ", funcName, " deviceId :", deviceId)
	var msg string
	var errInt int
	var res string
	var devId int
	requestId++

	switch deviceId {
	case "TMP-36":
		devId = DeviceIdTemp
		break
	case "PHANTOM-S1":
		devId = DeviceIdSound
		break
	}

	if funcName == FuncTftTempSend {
		msg = fmt.Sprintf("%7s%4d%4s", funcName, devId, tempResult)
	} else {
		msg = fmt.Sprintf("%7s%4d%4d", funcName, devId, requestId)
	}
	fmt.Println("Sending message(callXinu) : ", msg)

	if Conn2 != nil {
		_, err := Conn2.Write([]byte(msg))
		handleError(err)
		if err == nil {
			res, errInt = receiveXinuResponse(requestId, devId)
			return res, errInt
		}
	} else {
		fmt.Println("Conn is null")
		return "-1", 1
	}

	return "-1", 1

}

/*
 * Call Xinu for getting function result over UDP
 */
func callXinuFunction(funcName string, deviceId string) (string, int) {

	defer Conn.Close()

	fmt.Println("Calling Xinu function : ", funcName, " deviceId :", deviceId)
	var msg string
	var errInt int
	var res string
	var devId int
	requestId++

	switch deviceId {
	case "TMP-36":
		devId = DeviceIdTemp
		break
	case "PHANTOM-S1":
		devId = DeviceIdSound
		break
	}

	if funcName == FuncTftTempSend {
		msg = fmt.Sprintf("%7s%4d%4s", funcName, devId, tempResult)
	} else {
		msg = fmt.Sprintf("%7s%4d%4d", funcName, devId, requestId)
	}
	fmt.Println("Sending message(callXinu) : ", msg)

	if Conn != nil {
		_, err := Conn.Write([]byte(msg))
		handleError(err)
		if err == nil {
			res, errInt = receiveXinuResponse(requestId, devId)
			return res, errInt

		}
	} else {
		fmt.Println("Conn is null")

		return "-1", 1
	}

	return "-1", 1

}

/*
 * Write response back to client over HTTP
 */
func writeResponse(deviceId string, funcName string, result string, err int, w http.ResponseWriter) {
	var resultStr string

	switch funcName {
	case FuncGetTemp:
		resultStr = fmt.Sprintf(JsonResponseTemp, result, err)
		w.Write([]byte(resultStr))
		break
	case FuncGetSound:
		if result == "1111" {
			result = "RECEIVED"
		}
		resultStr = fmt.Sprintf(JsonResponseSound, result, err)
		w.Write([]byte(resultStr))
		break
	case FuncTftOn:
		resultStr = fmt.Sprintf(JsonResponseSoundTft, result, err)
		w.Write([]byte(resultStr))
		break
	case FuncTftOff:
		resultStr = fmt.Sprintf(JsonResponseSoundTft, result, err)
		w.Write([]byte(resultStr))
		break
	default:
		w.Write([]byte(ErrorJson))
		break
	}

}

/*
 * Handler code for interaction with client over HTTP
 */
func edgehandler(w http.ResponseWriter, r *http.Request) {
	var result string
	var err int
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")

	fmt.Println("RequestURI : ", r.RequestURI)

	queryMap := r.URL.Query()

	funcName := queryMap.Get("operation")
	deviceId := queryMap.Get("device")
	sensor := queryMap.Get("sensor")

	url := fmt.Sprintf(IpGetRequest, deviceId)

	response, er := http.Get(url)
	handleError(er)

	destipport, _ := ioutil.ReadAll(response.Body)

	n := bytes.IndexByte(destipport, byte(0))

	destip := string(destipport[:n])

	deviceIp = destip
	//fmt.Println("Ip from cloud:", len())
	if Conn != nil {
		Conn.Close()
	}
	if ServerConn != nil {
		ServerConn.Close()
	}

	initUDPConnection(destip)

	if funcName == "read" && sensor == "temperature" && deviceId == "TMP-36" {
		result, err = callXinuFunction(FuncGetTemp, deviceId)
		writeResponse(deviceId, FuncGetTemp, result, err, w)

	} else if funcName == "read" && sensor == "sound" && deviceId == "PHANTOM-S1" {
		result, err = callXinuFunction(FuncGetSound, deviceId)
		writeResponse(deviceId, FuncGetSound, result, err, w)

	} else if funcName == "tftmodeon" && sensor == "sound" && deviceId == "PHANTOM-S1" {
		url = fmt.Sprintf(IpGetRequest, "TMP-36")
		response, er = http.Get(url)
		handleError(er)
		destipport, _ = ioutil.ReadAll(response.Body)

		n = bytes.IndexByte(destipport, byte(0))

		destip = string(destipport[:n])

		initTftNetworkConf(destip)

		result, err = callXinuFunction(FuncTftModeOn, deviceId)
		fmt.Println("TFT_ON")
		writeResponse(deviceId, funcName, "TFT_ON", err, w)

	} else if funcName == "tftmodeoff" && sensor == "sound" && deviceId == "PHANTOM-S1" {

		result, err = callXinuFunction(FuncTftModeOff, deviceId)
		writeResponse(deviceId, funcName, "TFT_OFF", err, w)

	} else {
		writeResponse(deviceId, "", "-1", 1, w)
	}
}

func hostWebServer() {
	http.HandleFunc("/", edgehandler)
	http.ListenAndServe(":7000", nil)

}

/*
 * Control Server for receiving and updating control information with device and cloud
 */
func CtrlUdpServer() {

	fmt.Println("Starting UDP Control Component")

	buf := make([]byte, 1024)
	for {
		CtrlConn.SetReadDeadline(time.Now().Add(3 * time.Second))
		for {
			_, err := CtrlConn.Read(buf)
			if err != nil {
				if e, ok := err.(net.Error); !ok || !e.Timeout() {
				}
				break
			}

			fmt.Println("Ctrl data received : ", string(buf))

			devid, _ := strconv.Atoi(string(buf[:4]))
			devip := string(buf[4:])

			var devidstr string

			if devid == DeviceIdTemp {
				devidstr = DeviceNameTemp

			} else if devid == DeviceIdSound {
				devidstr = DeviceNameSound
			}

			reqString := fmt.Sprintf(IpUpdateRequest, devip, devidstr)
			fmt.Println("Request String :", reqString)
			http.Get(reqString)

		}

		time.Sleep(time.Millisecond * 100)
	}
}

/*
 * Main function
 */
func main() {

	requestId = InitialRequestId
	initNetworkConnection()
	go CtrlUdpServer()
	hostWebServer()

}
