using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace WpfDesktopApplicationv2.Models
{
    public class ServerIoT
    {
        public class IoTServer
        {
            private string ip;

            public IoTServer(string _ip)
            {
                ip = _ip;
            }

            /**
             * @brief obtaining the address of the data file from IoT server IP.
             */
            private string GetFileUrl()
            {
                return "http://" + ip + "/server/sensors_via_deamon.php?id=env";
            }

            private string GetFileUrlAngles()
            {
                return "http://" + ip + "/server/sensors_via_deamon.php?id=ori";
            }

            private string GetFileUrlJoy()
            {
                return "http://" + ip + "/server/joystick_via_deamon.php?id=get";
            }

            private string GetFileUrlJoyClear()
            {
                return "http://" + ip + "/server/joystick_via_deamon.php?id=rst";
            }


            /**
              * @brief HTTP GET request using HttpClient
              */
            public async Task<JArray> GETwithClient()
            {
                string responseText = null;

                try
                {
                    using (HttpClient client = new HttpClient())
                    {
                        responseText = await client.GetStringAsync(GetFileUrl());
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }
                
                return JArray.Parse(responseText);
            }

            public async Task<string> GETwithClientRPY()
            {
                string responseText = null;

                try
                {
                    using (HttpClient client = new HttpClient())
                    {
                        responseText = await client.GetStringAsync(GetFileUrlAngles());
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            public async Task<string> GETwithClientJoy()
            {
                HttpClient httpClient = new HttpClient();

                string responseText = await httpClient.GetStringAsync(GetFileUrlJoy());

                httpClient.Dispose();
                return responseText;
            }

            public async Task<string> GETwithClientJoyClear()
            {
                HttpClient httpClient = new HttpClient();

                string responseText = await httpClient.GetStringAsync(GetFileUrlJoyClear());

                httpClient.Dispose();
                return responseText;
            }

            /**
              * @brief HTTP POST request using HttpClient
             */
            public async Task<string> POSTwithClient()
            {
                string responseText = null;

                try
                {
                    using (HttpClient client = new HttpClient())
                    {
                        // POST request data
                        var requestDataCollection = new List<KeyValuePair<string, string>>();
                        requestDataCollection.Add(new KeyValuePair<string, string>("filename", "sensors_via_deamon.php"));
                        var requestData = new FormUrlEncodedContent(requestDataCollection);
                        // Sent POST request
                        var result = await client.PostAsync(GetFileUrl(), requestData);
                        // Read response content
                        responseText = await result.Content.ReadAsStringAsync();

                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            public async Task<string> POSTwithClientRPY()
            {
                string responseText = null;

                try
                {
                    using (HttpClient client = new HttpClient())
                    {
                        // POST request data
                        var requestDataCollection = new List<KeyValuePair<string, string>>();
                        requestDataCollection.Add(new KeyValuePair<string, string>("filename", "sensors_via_deamon.php"));
                        var requestData = new FormUrlEncodedContent(requestDataCollection);
                        // Sent POST request
                        var result = await client.PostAsync(GetFileUrlAngles(), requestData);
                        // Read response content
                        responseText = await result.Content.ReadAsStringAsync();

                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            public async Task<string> POSTwithClientJoy()
            {
                string responseText = null;

                try
                {
                    using (HttpClient client = new HttpClient())
                    {
                        // POST request data
                        var requestDataCollection = new List<KeyValuePair<string, string>>();
                        requestDataCollection.Add(new KeyValuePair<string, string>("filename", "joystick_via_deamon.php"));
                        var requestData = new FormUrlEncodedContent(requestDataCollection);
                        // Sent POST request
                        var result = await client.PostAsync(GetFileUrlJoy(), requestData);
                        // Read response content
                        responseText = await result.Content.ReadAsStringAsync();

                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            /**
              * @brief HTTP GET request using HttpWebRequest
              */
            public async Task<string> GETwithRequest()
            {
                string responseText = null;

                try
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(GetFileUrl());

                    request.Method = "GET";

                    using (HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        responseText = await reader.ReadToEndAsync();
                    }

                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            public async Task<string> GETwithRequestRPY()
            {
                string responseText = null;

                try
                {

                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(GetFileUrlAngles());

                    request.Method = "GET";

                    using (HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        responseText = await reader.ReadToEndAsync();
                    }

                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            public async Task<string> GETwithRequestJoy()
            {
                string responseText = null;

                try
                {

                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(GetFileUrlJoy());

                    request.Method = "GET";

                    using (HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        responseText = await reader.ReadToEndAsync();
                    }

                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }


            /**
              * @brief HTTP POST request using HttpWebRequest
              */
            public async Task<string> POSTwithRequest()
            {
                string responseText = null;

                try
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(GetFileUrl());

                    // POST Request data 
                    var requestData = "filename=sensors_via_deamon.php";
                    byte[] byteArray = Encoding.UTF8.GetBytes(requestData);
                    // POST Request configuration
                    request.Method = "POST";
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.ContentLength = byteArray.Length;
                    // Wrire data to request stream
                    Stream dataStream = request.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    using (HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        responseText = await reader.ReadToEndAsync();
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }


            public async Task<string> POSTwithRequestRPY()
            {
                string responseText = null;

                try
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(GetFileUrlAngles());

                    // POST Request data 

                    var requestData = "filename=sensors_via_deamon.php";
                    byte[] byteArray = Encoding.UTF8.GetBytes(requestData);
                    // POST Request configuration
                    request.Method = "POST";
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.ContentLength = byteArray.Length;
                    // Wrire data to request stream
                    Stream dataStream = request.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    using (HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        responseText = await reader.ReadToEndAsync();
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }

            public async Task<string> POSTwithRequestJoy()
            {
                string responseText = null;

                try
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(GetFileUrlJoy());

                    // POST Request data 

                    var requestData = "filename=joystick_via_deamon.php";
                    byte[] byteArray = Encoding.UTF8.GetBytes(requestData);
                    // POST Request configuration
                    request.Method = "POST";
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.ContentLength = byteArray.Length;
                    // Wrire data to request stream
                    Stream dataStream = request.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    using (HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        responseText = await reader.ReadToEndAsync();
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("NETWORK ERROR");
                    Debug.WriteLine(e);
                }

                return responseText;
            }


        }
    }
}
