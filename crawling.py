from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import time
from tqdm import tqdm
keyword = input('검색어를 입력하세요')


print('접속중')
driver = webdriver.Chrome('/usr/local/bin/chromedriver')
driver.implicitly_wait(30)



url = 'https://search.naver.com/search.naver?where=image&sm=tab_jum&query={}'.format(keyword)
driver.get(url)

#페이지 스크롤 다운
body = driver.find_element_by_css_selector('body')
for i in range(3):
    body.send_keys(Keys.PAGE_DOWN)
    time.sleep(1)

#이미지 링크 수
imgs = driver.find_elements_by_css_selector('img._img')
result = []
for img in tqdm(imgs):
    if 'http' in img.get_attribute('src'):
        result.append(img.get_attribute('src'))
driver.close()
print("수집완료")


#폴더 생성
import os
if not os.path.isdir('./{}'.format(keyword)):
    os.mkdir('./{}'.format(keyword))


#다운로드
from urllib.request import urlretrieve

for index, link in tqdm(enumerate(result)):
    start = link.rfind('.')
    end = link.rfind('&')

    filetype =link[start:end]
    urlretrieve(link, './{}/{}{}{}'.format(keyword,keyword,index,filetype))

# import zipfile
# zip_file = zipfile.ZipFile('./{}'.format(keyword), 'w')
#
# for image in os.listdir('./{]'.format(keyword)):
#     zip_file.write('./{}/{}'.format(keyword, image), compress_type= zipfile.ZIP_DEFLATED)
# zip_file.close()
# print("압축 완료")