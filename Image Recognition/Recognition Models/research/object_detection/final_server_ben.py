######## Webcam Object Detection Using Tensorflow-trained Classifier #########
#
# Author: Evan Juras
# Date: 1/20/18
# Description: 
# This program uses a TensorFlow-trained classifier to perform object detection.
# It loads the classifier and uses it to perform object detection on a webcam feed.
# It draws boxes, scores, and labels around the objects of interest in each frame
# from the webcam.

## Some of the code is copied from Google's example at
## https://github.com/tensorflow/models/blob/master/research/object_detection/object_detection_tutorial.ipynb

## and some is copied from Dat Tran's example at
## https://github.com/datitran/object_detector_app/blob/master/object_detection_app.py

## but I changed it to make it more understandable to me.


# Import packages
import os
import cv2
import numpy as np
import tensorflow as tf
import sys
import io
import socket
import struct
from PIL import Image, JpegImagePlugin as JIP, ImageOps
from collections import Counter


# Start a socket listening for connections on 0.0.0.0:8000 (0.0.0.0 means
# all interfaces)
server_socket = socket.socket()
server_socket.bind(('0.0.0.0', 8000))
server_socket.listen(0)

# Accept a single connection and make a file-like object out of it
connection = server_socket.accept()[0].makefile('rb')

# This is needed since the notebook is stored in the object_detection folder.
sys.path.append("..")

# Import utilites
from utils import label_map_util
from utils import visualization_utils as vis_util

# Name of the directory containing the object detection module we're using
MODEL_NAME = 'inference_graph_improved2'

# Grab path to current working directory
CWD_PATH = os.getcwd()

# Path to frozen detection graph .pb file, which contains the model that is used
# for object detection.
PATH_TO_CKPT = os.path.join(CWD_PATH,MODEL_NAME,'frozen_inference_graph.pb')

# Path to label map file
PATH_TO_LABELS = os.path.join(CWD_PATH,'training','labelmap.pbtxt')

# Number of classes the object detector can identify
NUM_CLASSES = 15

## Load the label map.
# Label maps map indices to category names, so that when our convolution
# network predicts `5`, we know that this corresponds to `king`.
# Here we use internal utility functions, but anything that returns a
# dictionary mapping integers to appropriate string labels would be fine
label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=NUM_CLASSES, use_display_name=True)
category_index = label_map_util.create_category_index(categories)

# Load the Tensorflow model into memory.
detection_graph = tf.Graph()
with detection_graph.as_default():
    od_graph_def = tf.GraphDef()
    with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
        serialized_graph = fid.read()
        od_graph_def.ParseFromString(serialized_graph)
        tf.import_graph_def(od_graph_def, name='')

    sess = tf.Session(graph=detection_graph)


# Define input and output tensors (i.e. data) for the object detection classifier

# Input tensor is the image
image_tensor = detection_graph.get_tensor_by_name('image_tensor:0')

# Output tensors are the detection boxes, scores, and classes
# Each box represents a part of the image where a particular object was detected
detection_boxes = detection_graph.get_tensor_by_name('detection_boxes:0')

# Each score represents level of confidence for each of the objects.
# The score is shown on the result image, together with the class label.
detection_scores = detection_graph.get_tensor_by_name('detection_scores:0')
detection_classes = detection_graph.get_tensor_by_name('detection_classes:0')

# Number of objects detected
num_detections = detection_graph.get_tensor_by_name('num_detections:0')

# Initialize webcam feed
#video = cv2.VideoCapture(0)
#ret = video.set(3,1280)
#ret = video.set(4,720)
output_list = []
output_img = np.empty((10, 400, 380, 3))
count = 0
while(True):
    # Read the length of the image as a 32-bit unsigned int. If the
    # length is zero, quit the loop
    image_len = struct.unpack('<L', connection.read(struct.calcsize('<L')))[0]
    if not image_len:
        break
    # Construct a stream to hold the image data and read the image
    # data from the connection
    image_stream = io.BytesIO()
    image_stream.write(connection.read(image_len))
    # Rewind the stream, open it as an image with PIL and do some
    # processing on it
    image_stream.seek(0)
    pil_image = Image.open(image_stream).convert('RGB')
    cv_image = np.array(pil_image)

    cv_image = cv_image[:,:,::-1].copy()

    # Acquire frame and expand frame dimensions to have shape: [1, None, None, 3]
    # i.e. a single-column array, where each item in the column has the pixel RGB value
    #ret, frame = video.read()
    frame_rgb = cv2.cvtColor(cv_image, cv2.COLOR_BGR2RGB)
    frame_expanded = np.expand_dims(frame_rgb, axis=0)

    # Perform the actual detection by running the model with the image as input
    (boxes, scores, classes, num) = sess.run(
        [detection_boxes, detection_scores, detection_classes, num_detections],
        feed_dict={image_tensor: frame_expanded})

    # Draw the results of the detection (aka 'visualize the results')
    vis_util.visualize_boxes_and_labels_on_image_array(
        cv_image,
        np.squeeze(boxes),
        np.squeeze(classes).astype(np.int32),
        np.squeeze(scores),
        category_index,
        use_normalized_coordinates=True,
        line_thickness=8,
        min_score_thresh=0.60)

    for index, value in enumerate(classes[0]):
        if scores[0, index] > 0.6:
            output_list.append(category_index.get(value)['id'])
            temp = cv2.cvtColor(cv_image, cv2.COLOR_BGR2RGB)
            if count == 0:
                a = temp
            elif count == 1:
                b = temp
            elif count == 2:
                c = temp
            elif count == 3:
                d = temp
            else:
                e = temp

            # output_img[count] = temp
            count += 1

    if len(output_list) > 5:
        print(Counter(output_list).most_common(1)[0][0])
        # check for the highest output for past 5 captured frames. Make sure we only save img with the right output
        for i in range(5):
            if output_list[i] == Counter(output_list).most_common(1)[0][0]:

                if i == 0:
                    saver = Image.fromarray(a)
                elif i == 1:
                    saver = Image.fromarray(b)
                elif i == 2:
                    saver = Image.fromarray(c)
                elif i == 3:
                    saver = Image.fromarray(d)
                else:
                    saver = Image.fromarray(e)
                # dont need to reset abcde as it will change every iteration

                # b = (output_img[i] * 255).astype(np.uint8)
                # b = b[..., [1,0,2]]
                # saver = Image.fromarray(b)
                saver.save("C:/Users/lowbe/Documents/tensorflow1/detected_img/" + str(Counter(output_list).most_common(1)[0][0]) + ".jpg",
                           subsampling=JIP.get_sampling(saver),
                           format="JPEG")

                output_img = np.zeros((10, 400, 380, 3))
                output_list = []
                count = 0
                break



    # All the results have been drawn on the frame, so it's time to display it.
    cv2.imshow('Object detector', cv_image)
    
    
    # Press 'q' to quit
    if cv2.waitKey(1) == ord('q'):
        break

# Clean up
cv2.destroyAllWindows()